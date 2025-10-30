package com.example.kns.auth_screen

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kns.data.RecordDatabase
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class DashboardItem(val icon: ImageVector, val label: String, val route: String)

@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val db = RecordDatabase.getDatabase(context)
    val scope = rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                scope.launch {
                    exportDataToCsv(context, db)
                }
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val items = listOf(
        DashboardItem(Icons.Default.Upload, "Import Data", "import_data"),
        DashboardItem(Icons.Default.Search, "Search Data", "search"),
        DashboardItem(Icons.Default.Add, "Add Record", "add_record"),
        DashboardItem(Icons.Default.Download, "Export Data", "export_data"),
        DashboardItem(Icons.Default.Delete, "Delete Record", "delete_record"),
        DashboardItem(Icons.Default.List, "View All Records", "view_all_records"),
        DashboardItem(Icons.Default.ExitToApp, "Exit / Logout", "login")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                DashboardButton(item = item, navController = navController) {
                    if (item.route == "export_data") {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            scope.launch {
                                exportDataToCsv(context, db)
                            }
                        } else {
                            permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardButton(item: DashboardItem, navController: NavController, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            if (item.route.isNotEmpty()) {
                if (item.route == "login") {
                    navController.navigate(item.route) {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                } else if (item.route != "export_data") {
                    navController.navigate(item.route)
                } else {
                    onClick()
                }
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = item.icon, contentDescription = item.label, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.label, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

private suspend fun exportDataToCsv(context: Context, db: RecordDatabase) {
    val records = db.recordDao().getAllRecordsList()
    val timeStamp = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
    val fileName = "KNS_Export_$timeStamp.csv"

    val header = "Name,Aadhaar,PAN,DOB,Mobile,Bank Account,CIF,Address,Remark\n"
    val content = records.joinToString(separator = "\n") {
        "\"${it.name}\",\"${it.aadhaar}\",\"${it.pan}\",\"${it.dob}\",\"${it.mobile}\",\"${it.bankAccount}\",\"${it.cif}\",\"${it.address}\",\"${it.remark}\""
    }

    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/csv")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Documents/KNS_Exports")
            }
            val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { outputStream ->
                    outputStream.write((header + content).toByteArray())
                }
                Toast.makeText(context, "Data exported successfully to Documents/KNS_Exports", Toast.LENGTH_LONG).show()
            } ?: throw IOException("Failed to create new MediaStore record.")
        } else {
            // For older versions, you'll use the permission-based approach.
        }
    } catch (e: IOException) {
        Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
