package nz.ac.canterbury.seng303.betzero

import AnalyticsScreen
import PopupScreen
import SummariesScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Sos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.compose.BetzeroTheme
import nz.ac.canterbury.seng303.betzero.screens.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BetzeroTheme {
                val showPopup = remember { mutableStateOf(true) }
                val navController = rememberNavController()
                val iconModifier = Modifier.size(50.dp)
                val iconColor = MaterialTheme.colorScheme.primary

                Scaffold(
                    topBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        TopAppBar(
                            title = { Text("BetZero") },
                            actions = {
                                if (currentDestination?.route !in listOf("OnBoardingScreen", "GettingStartedScreen")) {
                                    IconButton(onClick = { navController.navigate("UserProfileScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.AccountCircle,
                                            contentDescription = "Profile",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        if (currentDestination?.route !in listOf("OnBoardingScreen", "GettingStartedScreen")) {
                            BottomAppBar(
                                modifier = Modifier.height(60.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    IconButton(onClick = { navController.navigate("CalendarScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Calendar",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                    IconButton(onClick = { navController.navigate("AnalyticsScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.AttachMoney,
                                            contentDescription = "Analytics",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                    IconButton(onClick = { navController.navigate("Home") }) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = "Home",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                    IconButton(onClick = { navController.navigate("SummariesScreen") }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.List,
                                            contentDescription = "Summaries",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                    IconButton(onClick = { navController.navigate("EmergencyScreen") }) {
                                        Icon(
                                            imageVector = Icons.Default.Sos,
                                            contentDescription = "SOS",
                                            modifier = iconModifier,
                                            tint = iconColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Box(modifier = Modifier.padding(it)) {
                        NavHost(navController = navController, startDestination = "InitialScreen") {
                            composable("InitialScreen") {
                                InitialScreen(navController = navController)
                            }
                            composable("OnBoardingScreen") {
                                OnboardingScreen(navController = navController)
                            }
                            composable("CalendarScreen") {
                                CalendarScreen(navController = navController)
                            }
                            composable("AnalyticsScreen") {
                                AnalyticsScreen(navController = navController)
                            }
                            composable("Home") {
                                Home(navController = navController)
                            }
                            composable("SummariesScreen") {
                                SummariesScreen(navController = navController)
                            }
                            composable("EmergencyScreen") {
                                EmergencyScreen(navController = navController)
                            }
                            composable("GettingStartedScreen") {
                                GettingStartedScreen(navController = navController)
                            }
                            composable("UserProfileScreen") {
                                UserProfileScreen(navController = navController)
                            }
                            composable("UpdateUserProfileScreen") {
                                UpdateUserProfileScreen(navController = navController)
                            }
                        }

                        if (showPopup.value) {
                            Dialog(onDismissRequest = { showPopup.value = false }) {
                                PopupScreen(
                                    onDismiss = { showPopup.value = false },
                                    onSave = { dailyLog ->
                                        // Handle any additional actions after saving, if needed
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Home(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Main Screen")
    }
}