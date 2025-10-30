package com.example.kns.csv_import

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.kns.data.Record
import com.example.kns.data.RecordDatabase
import kotlinx.coroutines.launch
import java.io.InputStreamReader

@Composable
fun ImportScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { RecordDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    // State saved across rotation
    var importStatus   by rememberSaveable { mutableStateOf("") }
    var isImporting    by rememberSaveable { mutableStateOf(false) }
    var progress       by rememberSaveable { mutableStateOf(0f) }
    var totalLines     by rememberSaveable { mutableStateOf(0) }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val fileName = getFileNameFromUri(context, selectedUri)
            if (!fileName.endsWith(".csv", ignoreCase = true)) {
                importStatus = "Please select a .csv file."
                return@let
            }

            scope.launch {
                isImporting = true
                importStatus = "Preparing…"
                progress = 0f
                totalLines = 0

                try {
                    val records = mutableListOf<Record>()

                    // 1. Count total rows
                    context.contentResolver.openInputStream(selectedUri)?.use { stream ->
                        InputStreamReader(stream).buffered().useLines { lines ->
                            totalLines = lines.count() - 1
                        }
                    }

                    if (totalLines <= 0) {
                        importStatus = "CSV is empty."
                        isImporting = false
                        return@launch
                    }

                    // 2. Import with progress
                    var processed = 0
                    context.contentResolver.openInputStream(selectedUri)?.use { stream ->
                        InputStreamReader(stream).buffered().useLines { lines ->
                            val it = lines.iterator()
                            it.next() // skip header

                            for (line in it) {
                                val tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)".toRegex())
                                    .map { it.trim().removeSurrounding("\"") }

                                if (tokens.size >= 9) {
                                    records.add(
                                        Record(
                                            name = tokens[0],
                                            aadhaar = tokens[1],
                                            pan = tokens[2],
                                            dob = tokens[3],
                                            mobile = tokens[4],
                                            bankAccount = tokens[5],
                                            cif = tokens[6],
                                            address = tokens[7],
                                            remark = tokens[8],
                                            imageUri = null
                                        )
                                    )
                                }

                                processed++
                                progress = processed.toFloat() / totalLines.toFloat()
                            }
                        }
                    }

                    db.recordDao().insertAll(records)
                    importStatus = "Successfully imported ${records.size} records."
                } catch (e: Exception) {
                    importStatus = "Error: ${e.message}"
                    e.printStackTrace()
                } finally {
                    isImporting = false
                    progress = 1f
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Import From CSV", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = { filePicker.launch("*/*") }) {
            Text("Select CSV File")
        }

        Spacer(modifier = Modifier.height(32.dp))

        when {
            isImporting -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(0.8f).height(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${(progress * 100).toInt()} %  ($totalLines rows)")
                }
            }

            importStatus.isNotEmpty() -> {
                Text(
                    text = importStatus,
                    color = if (importStatus.startsWith("Successfully"))
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))

                // OK Button → Go back to Dashboard
                Button(onClick = {
                    importStatus = ""
                    navController.popBackStack() // ← Returns to Dashboard
                }) {
                    Text("OK")
                }
            }
        }
    }
}

private fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "unknown.csv"
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (idx != -1 && cursor.moveToFirst()) {
            name = cursor.getString(idx)
        }
    }
    return name
}