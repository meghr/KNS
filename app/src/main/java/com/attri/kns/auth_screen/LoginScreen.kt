package com.attri.kns.auth_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.attri.kns.AuthStore
import com.attri.kns.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val authStore = AuthStore(context)

    // ✅ Use scrollable column to automatically adjust when keyboard opens
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding() // adds padding when keyboard appears
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .wrapContentHeight(Alignment.Top), // push content toward top
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp)) // top padding

        // 🔹 Profile photo (shifted slightly upward)
        Image(
            painter = painterResource(id = R.drawable.my_photo),
            contentDescription = "My Photo",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        val textStyle = TextStyle(fontWeight = FontWeight.Bold)
        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )

        // 🔹 Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            textStyle = textStyle,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🔹 Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = textStyle,
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (showError) {
            Text(
                text = "Invalid username or password",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 🔹 Login button
        Button(
            onClick = {
                val storedUsername = authStore.getUsername()
                val storedPassword = authStore.getPassword()

                if (storedUsername != null && storedPassword != null) {
                    if (username == storedUsername && password == storedPassword) {
                        navController.navigate("dashboard")
                    } else {
                        showError = true
                    }
                } else {
                    if (username == "123" && password == "123") {
                        navController.navigate("dashboard")
                    } else {
                        showError = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(40.dp)) // small gap before bottom
    }
}
