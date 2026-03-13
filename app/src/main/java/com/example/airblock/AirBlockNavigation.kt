package com.example.airblock

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.airblock.ui.AirBlockHomeScreen
import com.example.airblock.ui.screens.SettingsScreen
import com.example.airblock.ui.screens.AppBlockingScreen
import com.example.airblock.ui.screens.resetNfcTag

@Composable
fun AirBlockAppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current


    NavHost(navController = navController, startDestination = "home") {

        // --- RUTA 1: Pantalla Principal ---
        composable("home") {
            AirBlockHomeScreen(

                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToAppBlocking = { navController.navigate("apps") }
            )
        }

        // --- RUTA 2: Ajustes ---
        composable("settings") {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onResetTag = {
                    resetNfcTag(context)
                    navController.popBackStack()
                }
            )
        }

        // --- RUTA 3: Bloqueo de Apps ---
        composable("apps") {
            AppBlockingScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}