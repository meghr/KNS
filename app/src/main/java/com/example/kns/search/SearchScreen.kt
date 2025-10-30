package com.example.kns.search

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.kns.data.Record
import androidx.navigation.NavController
import com.example.kns.data.RecordDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val db = remember { RecordDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Record>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("Name") }
    val filterOptions = listOf("Name", "Aadhaar", "PAN", "Account No", "CIF")
    var expanded by remember { mutableStateOf(false) }
    var hasSearched by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Search Records", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = selectedFilter,
                    onValueChange = { },
                    label = { Text("Filter By") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    filterOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(text = selectionOption) },
                            onClick = {
                                selectedFilter = selectionOption
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Enter Search Term") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Button(onClick = {
                hasSearched = true
                scope.launch {
                    val query = "%${searchQuery}%"
                    searchResults = when (selectedFilter) {
                        "Name" -> db.recordDao().findByName(query).first()
                        "Aadhaar" -> db.recordDao().findByAadhaar(query).first()
                        "PAN" -> db.recordDao().findByPan(query).first()
                        "Account No" -> db.recordDao().findByBankAccount(query).first()
                        "CIF" -> db.recordDao().findByCif(query).first()
                        else -> emptyList()
                    }
                }
            }) {
                Text("Search")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                searchQuery = ""
                searchResults = emptyList()
                hasSearched = false
            }) {
                Text("Clear Search")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (hasSearched && searchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No records found with the specified ${selectedFilter}.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(searchResults) { record ->
                    RecordCard(record = record, navController = navController)
                }
            }
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun RecordCard(record: Record, navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 2 })

    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 32.dp)
        ) { page ->
            if (page == 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = record.name,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        @Composable
                        fun RecordDetail(label: String, value: String?) {
                            if (!value.isNullOrBlank()) {
                                Text(
                                    text = "$label: $value",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        RecordDetail("Aadhaar", record.aadhaar)
                        RecordDetail("PAN", record.pan)
                        RecordDetail("DOB", record.dob)
                        RecordDetail("Mobile", record.mobile)
                        RecordDetail("Account No", record.bankAccount)
                        RecordDetail("CIF", record.cif)
                        RecordDetail("Address", record.address)
                        RecordDetail("Remark", record.remark)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!record.imageUri.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.parse(record.imageUri)),
                                contentDescription = "Record Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Photo",
                                modifier = Modifier.size(120.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier
                .height(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
        Button(onClick = { navController.navigate("edit_record/${record.id}") }) {
            Text("Edit")
        }
    }
}
