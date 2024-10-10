package nz.ac.canterbury.seng303.betzero.viewmodels

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import nz.ac.canterbury.seng303.betzero.datastore.Storage
import nz.ac.canterbury.seng303.betzero.models.UserProfile

class PreferencesViewModel (
    private val userProfileStorage: Storage<UserProfile>,
) : ViewModel() {
    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> get() = _userProfile

    private var isSystemInDarkTheme: Boolean = false

    private val _isDarkTheme = MutableStateFlow(isSystemInDarkTheme) // Initially follow system theme
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    private fun updateTheme() {
        viewModelScope.launch {
            _userProfile.value?.let { profile ->
                if (profile.isUserEnforcedTheme) {
                    _isDarkTheme.emit(profile.isDarkMode)
                } else {
                    _isDarkTheme.emit(isSystemInDarkTheme)
                }
            }
        }
    }

    fun setIsSystemInDarkTheme(enabled : Boolean) {
        isSystemInDarkTheme = enabled
        updateTheme()
    }

    fun updateThemeSettings(selectedOption: Int) {
        viewModelScope.launch {
            val currentProfile = userProfile.firstOrNull()
            var isUserEnforcedTheme = false
            var isDarkMode = false

            // Check if the currentProfile is null before proceeding
            if (currentProfile != null) {

                when (selectedOption) {
                    0 -> { // Light
                        isUserEnforcedTheme = true
                        isDarkMode = false
                    }
                    1 -> { // System
                        isUserEnforcedTheme = false
                        isDarkMode = isSystemInDarkTheme
                    }
                    2 -> { // Dark
                        isUserEnforcedTheme = true
                        isDarkMode = true
                    }
                    else -> {
                        return@launch
                    }

                }

                val updatedProfile = UserProfile(
                    currentProfile.id,
                    currentProfile.name,
                    currentProfile.totalSpent,
                    currentProfile.totalSaved,
                    currentProfile.dailySavings,
                    currentProfile.gamblingStartDate,
                    currentProfile.lastGambledDate,
                    isDarkMode,
                    isUserEnforcedTheme
                )

                // Use the edit method to update the user profile in storage
                userProfileStorage.edit(currentProfile.id, updatedProfile)
                updateTheme()
            }
        }
    }

    init {
        viewModelScope.launch {
            try {
                val userProfiles = userProfileStorage.getAll().first()
                if (userProfiles.isNotEmpty()) {
                    _userProfile.value = userProfiles.first()
                    updateTheme()
                } else {
                    _userProfile.value = null
                }
            } catch (e: Exception) {
                _userProfile.value = null
            }
        }
    }
}