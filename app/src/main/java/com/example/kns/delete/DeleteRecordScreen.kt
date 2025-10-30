package com.example.kns.delete

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.kns.data.Record
import com.example.kns.data.RecordDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteRecordScreen() {
    val context = LocalContext.current
    val db = remember { RecordDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Record>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<Record?>(null) }
    var hasSearched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Delete Record", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search by Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            hasSearched = true
            scope.launch {
                searchResults = db.recordDao().findByName("%${searchQuery}%").first()
            }
        }) {
            Text("Search")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hasSearched && searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No records found with that name.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(searchResults) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { // Not working as of now but will fix it in next iteration
                                recordToDelete = record
                                showDialog = true
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = record.name, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Aadhaar: ${record.aadhaar}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this record permanently?") },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        recordToDelete?.let { db.recordDao().delete(it) }
                        showDialog = false
                        searchResults = db.recordDao().findByName("%${searchQuery}%").first()
                    }
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
