package nz.ac.canterbury.seng303.betzero.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nz.ac.canterbury.seng303.betzero.models.Identifiable
import java.lang.reflect.Type

class PersistentStorage<T>(
    private val gson: Gson,
    private val type: Type,
    private val dataStore: DataStore<Preferences>,
    private val preferenceKey: Preferences.Key<String>
) : Storage <T> where T: Identifiable {

    override fun insert(data: T): Flow<Int> = flow {
        withContext(Dispatchers.IO) {
            val cachedDataClone = getAll().first().toMutableList()
            cachedDataClone.add(data)
            dataStore.edit { preferences ->
                val jsonString = gson.toJson(cachedDataClone, type)
                preferences[preferenceKey] = jsonString
            }
        }
        emit(OPERATION_SUCCESS)
    }

    override fun insertAll(data: List<T>): Flow<Int> {
        return flow {
            val cachedDataClone = getAll().first().toMutableList()
            cachedDataClone.addAll(data)
            dataStore.edit {
                val jsonString = gson.toJson(cachedDataClone, type)
                it[preferenceKey] = jsonString
                emit(OPERATION_SUCCESS)
            }
        }
    }

    override fun getAll(): Flow<List<T>> {
        return dataStore.data.map { preferences ->
            val jsonString = preferences[preferenceKey] ?: EMPTY_JSON_STRING
            val elements = gson.fromJson<List<T>>(jsonString, type)
            elements
        }
    }

    override fun edit(identifier: Int, data: T): Flow<Int> {
        return channelFlow {
            val flowContext = currentCoroutineContext()

            val loading: Job = coroutineScope {
                launch(flowContext) {
                    val cachedDataClone = getAll().first().toMutableList()
                    val index = cachedDataClone.indexOfFirst { it.getIdentifier() == identifier }

                    if (index != -1) {
                        cachedDataClone[index] = data

                        dataStore.edit { preferences ->
                            val jsonString = gson.toJson(cachedDataClone, type)
                            preferences[preferenceKey] = jsonString
                        }

                        send(OPERATION_SUCCESS)
                    } else {
                        send(OPERATION_FAILURE)
                    }
                }
            }

            loading.join()
        }.flowOn(Dispatchers.IO)
    }

    override fun get(where: (T) -> Boolean): Flow<T> {
        return flow {
            val data = getAll().first().first(where)
            emit(data)
        }
    }

    override fun delete(identifier: Int): Flow<Int> = channelFlow {
        val cachedDataClone = getAll().first().toMutableList()
        val updatedData = cachedDataClone.filterNot { it.getIdentifier() == identifier }

        dataStore.edit {
            val jsonString = gson.toJson(updatedData, type)
            it[preferenceKey] = jsonString
            trySend(OPERATION_SUCCESS)
        }
        awaitClose { /* should handle errors but cbf */ }
    }

    companion object {
        private const val OPERATION_SUCCESS = 1
        private const val OPERATION_FAILURE = -1
        private const val EMPTY_JSON_STRING = "[]"
    }
}
