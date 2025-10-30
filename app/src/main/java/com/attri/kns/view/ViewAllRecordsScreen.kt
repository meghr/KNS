package com.attri.kns.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.attri.kns.data.RecordDatabase

@Composable
fun ViewAllRecordsScreen() {
    val context = LocalContext.current
    val db = remember { RecordDatabase.getDatabase(context) }
    val records by db.recordDao().getAllRecords().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "All Records", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(Modifier.fillMaxWidth().background(Color.White)) {
            Text("S.N.", modifier = Modifier.weight(0.5f).padding(8.dp), fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Name", modifier = Modifier.weight(1.5f).padding(8.dp), fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Aadhaar", modifier = Modifier.weight(1.5f).padding(8.dp), fontWeight = FontWeight.Bold, color = Color.Black)
            Text("Mobile", modifier = Modifier.weight(1f).padding(8.dp), fontWeight = FontWeight.Bold, color = Color.Black)
        }

        LazyColumn {
            itemsIndexed(records) { index, record ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    Text((index + 1).toString(), modifier = Modifier.weight(0.5f).padding(8.dp), color = Color.Black)
                    Text(record.name, modifier = Modifier.weight(1.5f).padding(8.dp), color = Color.Black)
                    Text(record.aadhaar, modifier = Modifier.weight(1.5f).padding(8.dp), color = Color.Black)
                    Text(
                        text = record.mobile,
                        modifier = Modifier
                            .weight(1f)
                            .padding(8.dp)
                            .clickable { 
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${record.mobile}"))
                                context.startActivity(intent)
                            },
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
