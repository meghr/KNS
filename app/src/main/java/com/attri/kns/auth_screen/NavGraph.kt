package com.attri.kns.auth_screen

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.attri.kns.csv_import.ImportScreen
import com.attri.kns.delete.DeleteRecordScreen
import com.attri.kns.records.AddRecordScreen
import com.attri.kns.search.SearchScreen
import com.attri.kns.view.ViewAllRecordsScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController)
        }
        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
        composable("add_record") {
            AddRecordScreen(navController = navController)
        }
        composable("search") {
            SearchScreen()
        }
        composable("delete_record") {
            DeleteRecordScreen()
        }
        composable("import_data") {
            ImportScreen(navController = navController)   // ← NOW PASSES navController
        }
        composable("view_all_records") {
            ViewAllRecordsScreen()
        }
    }
}