// MainActivity.kt
package com.example.dhtapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // Untuk tampilan edge-to-edge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.dhtapp.ui.screens.DhtScreen
import com.example.dhtapp.ui.screens.HistoryScreen
import com.example.dhtapp.ui.screens.MainViewModel
import com.example.dhtapp.ui.splash.SplashScreen
import com.example.dhtapp.ui.theme.DhtAppTheme

// --- Definisi Rute/Screen yang Benar ---


sealed class Screen(val route: String) {
    object splash : Screen("splash_screen")
    object DhtMonitor : Screen("dht_monitor_route")
    object History : Screen("history_route")
}
// --- Akhir Definisi Rute/Screen ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DhtAppTheme {
                val navController = rememberNavController() // Satu-satunya NavController yang dibuat
                AppNavHost(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {


    NavHost(navController = navController, startDestination = Screen.splash.route) {
        composable(Screen.splash.route){
            SplashScreen(onTimeout = {
                navController.navigate(Screen.DhtMonitor.route){
                    popUpTo(Screen.splash.route){inclusive = true}
                }
            })
        }
        composable(Screen.DhtMonitor.route) {
            val mainViewModel: MainViewModel = viewModel()
            DisposableEffect(mainViewModel) {
                mainViewModel.startFetchingDataPeriodically()
                onDispose {
                    mainViewModel.stopFetchingDataPeriodically()
                }
            }
            DhtScreen(
                viewModel = mainViewModel,
                onViewHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DhtAppTheme {
        AppNavHost(rememberNavController())
    }
}