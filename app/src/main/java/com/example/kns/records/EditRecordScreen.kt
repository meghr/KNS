package com.example.kns.records

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.kns.data.Record
import com.example.kns.data.RecordDatabase
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

private fun saveImageToInternalStorage(context: Context, uriString: String): Uri? {
    val uri = Uri.parse(uriString)
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileName = "IMG_${System.currentTimeMillis()}.jpg"
        val file = File(context.filesDir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        file.toUri()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditRecordScreen(navController: NavController, record: Record) {
    var name by remember { mutableStateOf(record.name) }
    var aadhaar1 by remember { mutableStateOf(record.aadhaar.substring(0, 4)) }
    var aadhaar2 by remember { mutableStateOf(record.aadhaar.substring(5, 9)) }
    var aadhaar3 by remember { mutableStateOf(record.aadhaar.substring(10, 14)) }
    var pan by remember { mutableStateOf(record.pan) }
    val dobParts = record.dob.split("-")
    var dobDay by remember { mutableStateOf(dobParts.getOrElse(0) { "" }) }
    var dobMonth by remember { mutableStateOf(dobParts.getOrElse(1) { "" }) }
    var dobYear by remember { mutableStateOf(dobParts.getOrElse(2) { "" }) }
    var mobile by remember { mutableStateOf(record.mobile) }
    var bankAccount by remember { mutableStateOf(record.bankAccount) }
    var cif by remember { mutableStateOf(record.cif) }
    var address by remember { mutableStateOf(record.address) }
    var remark by remember { mutableStateOf(record.remark) }
    var imageUri by remember { mutableStateOf(record.imageUri?.let { Uri.parse(it) }) }

    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val db = remember { RecordDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val (aadhaar1Focus, aadhaar2Focus, aadhaar3Focus, dobMonthFocus, dobYearFocus) = remember { FocusRequester.createRefs() }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                imageUri = saveImageToInternalStorage(context, it.toString())
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Record", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected photo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { galleryLauncher.launch("image/*") }) {
            Text("Upload Photo")
        }
        Spacer(modifier = Modifier.height(16.dp))

        val textStyle = TextStyle(fontWeight = FontWeight.Bold)
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name*") }, textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = aadhaar1,
                onValueChange = {
                    if (it.length <= 4) {
                        aadhaar1 = it
                        if (it.length == 4) {
                            aadhaar2Focus.requestFocus()
                        }
                    }
                },
                label = { Text("Aadhaar*") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).focusRequester(aadhaar1Focus),
                textStyle = textStyle,
                colors = textFieldColors
            )
            Text(text = "-", modifier = Modifier.padding(horizontal = 4.dp))
            OutlinedTextField(
                value = aadhaar2,
                onValueChange = {
                    if (it.length <= 4) {
                        aadhaar2 = it
                        if (it.length == 4) {
                            aadhaar3Focus.requestFocus()
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).focusRequester(aadhaar2Focus),
                textStyle = textStyle,
                colors = textFieldColors
            )
            Text(text = "-", modifier = Modifier.padding(horizontal = 4.dp))
            OutlinedTextField(
                value = aadhaar3,
                onValueChange = {
                    if (it.length <= 4) {
                        aadhaar3 = it
                        if (it.length == 4) {
                            focusManager.clearFocus()
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).focusRequester(aadhaar3Focus),
                textStyle = textStyle,
                colors = textFieldColors
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = pan, onValueChange = { pan = it }, label = { Text("PAN Number") }, textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = dobDay,
                onValueChange = {
                    if (it.length <= 2) {
                        val day = it.toIntOrNull()
                        if (day == null || day in 1..31) {
                            dobDay = it
                        }
                        if (it.length == 2) {
                            dobMonthFocus.requestFocus()
                        }
                    }
                },
                placeholder = { Text("DD") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                textStyle = textStyle,
                colors = textFieldColors
            )
            Text(text = "-", modifier = Modifier.padding(horizontal = 4.dp))
            OutlinedTextField(
                value = dobMonth,
                onValueChange = {
                    if (it.length <= 2) {
                        val month = it.toIntOrNull()
                        if (month == null || month in 1..12) {
                            dobMonth = it
                        }
                        if (it.length == 2) {
                            dobYearFocus.requestFocus()
                        }
                    }
                },
                placeholder = { Text("MM") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).focusRequester(dobMonthFocus),
                textStyle = textStyle,
                colors = textFieldColors
            )
            Text(text = "-", modifier = Modifier.padding(horizontal = 4.dp))
            OutlinedTextField(
                value = dobYear,
                onValueChange = {
                    if (it.length <= 4) {
                        dobYear = it
                        if (it.length == 4) {
                            focusManager.clearFocus()
                        }
                    }
                },
                placeholder = { Text("YYYY") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1.5f).focusRequester(dobYearFocus),
                textStyle = textStyle,
                colors = textFieldColors
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = mobile, onValueChange = { if (it.length <= 10) mobile = it }, label = { Text("Mobile Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = bankAccount, onValueChange = { bankAccount = it }, label = { Text("Saving Bank Account Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = cif, onValueChange = { cif = it }, label = { Text("CIF Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Address") }, textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = remark, onValueChange = { remark = it }, label = { Text("Remark") }, textStyle = textStyle, colors = textFieldColors)
        Spacer(modifier = Modifier.height(16.dp))

        if (showError) {
            Text("Please fill all mandatory fields.", color = MaterialTheme.colorScheme.error)
        }

        if (showSuccess) {
            Text("Record updated successfully.", color = MaterialTheme.colorScheme.primary)
        }

        Button(onClick = {
            if (name.isNotEmpty() && aadhaar1.length == 4 && aadhaar2.length == 4 && aadhaar3.length == 4) {
                val updatedRecord = Record(
                    id = record.id,
                    name = name,
                    aadhaar = "$aadhaar1-$aadhaar2-$aadhaar3",
                    pan = pan.uppercase(),
                    dob = "$dobDay-$dobMonth-$dobYear",
                    mobile = mobile,
                    bankAccount = bankAccount,
                    cif = cif,
                    address = address,
                    remark = remark,
                    imageUri = imageUri?.toString()
                )
                scope.launch {
                    db.recordDao().update(updatedRecord)
                }

                showSuccess = true
                showError = false
                navController.navigateUp()
            } else {
                showError = true
                showSuccess = false
            }
        }) {
            Text("Update Data")
        }
    }
}
