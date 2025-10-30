package com.example.kns.auth_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import android.content.Context
import androidx.navigation.compose.rememberNavController
import com.example.kns.csv_import.ImportScreen
import com.example.kns.data.Record
import com.example.kns.data.RecordDatabase
import com.example.kns.delete.DeleteRecordScreen
import com.example.kns.records.AddRecordScreen
import com.example.kns.records.EditRecordScreen
import com.example.kns.search.SearchScreen
import com.example.kns.view.ViewAllRecordsScreen

@Composable
fun NavGraph(context: Context) {
    val navController = rememberNavController()
    val db = RecordDatabase.getDatabase(context)

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
            SearchScreen(navController = navController)
        }
        composable("edit_record/{recordId}") { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")?.toIntOrNull()
            if (recordId != null) {
                var record by remember { mutableStateOf<Record?>(null) }
                LaunchedEffect(recordId) {
                    record = db.recordDao().getRecordById(recordId)
                }
                record?.let {
                    EditRecordScreen(navController = navController, record = it)
                }
            }
        }
        composable("delete_record") {
            DeleteRecordScreen()
        }
        composable("import_data") { 
            ImportScreen(navController = navController)
        }
        composable("view_all_records") { 
            ViewAllRecordsScreen()
        }
    }
}