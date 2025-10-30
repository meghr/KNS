package com.attri.kns.auth_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.attri.kns.AuthStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeCredentialsScreen(navController: NavController) {
    var newUsername by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authStore = AuthStore(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Change Credentials", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = newUsername,
            onValueChange = { newUsername = it },
            label = { Text("New Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (showError) {
            Text("Passwords do not match.", color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showSuccess) {
            Text("Credentials changed successfully.", color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (newPassword == confirmPassword) {
                    authStore.setCredentials(newUsername, newPassword)
                    showSuccess = true
                    showError = false
                } else {
                    showError = true
                    showSuccess = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Changes")
        }
    }
}
