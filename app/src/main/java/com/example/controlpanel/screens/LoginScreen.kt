package com.example.controlpanel.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
//    SideEffect {
//        systemUiController.setStatusBarColor(
//            color = Color.Black,
//            darkIcons = false // إذا كنت تريد أيقونات بيضاء (لشريط الحالة الداكن)
//        )
//    }
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val auth = remember { FirebaseAuth.getInstance() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(

        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Admin Login",
            style = MaterialTheme.typography.titleLarge,
            color = Color(0xFFD4AF37) // لون ذهبي
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(

            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD4AF37), // لون الحدود عند التركيز (ذهبي)
                unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f), // لون الحدود بدون تركيز (ذهبي باهت)
                focusedLabelColor = Color(0xFFD4AF37), // لون النص عند التركيز
                unfocusedLabelColor = Color(0xFFD4AF37).copy(alpha = 0.5f), // لون النص بدون تركيز
                focusedTextColor = Color(0xFFD4AF37), // لون النص المدخل عند التركيز (ذهبي)
                unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD4AF37), // لون الحدود عند التركيز (ذهبي)
                unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f), // لون الحدود بدون تركيز (ذهبي باهت)
                focusedLabelColor = Color(0xFFD4AF37), // لون النص عند التركيز
                unfocusedLabelColor = Color(0xFFD4AF37).copy(alpha = 0.5f), // لون النص بدون تركيز
                focusedTextColor = Color(0xFFD4AF37), // لون النص المدخل عند التركيز (ذهبي)
                unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4AF37),
                contentColor = Color.White),
            onClick = {
                keyboardController?.hide()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Email and password cannot be empty", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!isValidEmail(email)) {
                    Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                isLoading = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            email = ""
                            password = ""
                            navController.navigate("add_code")
                        } else {
                            Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Login")
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    return email.matches(emailRegex.toRegex())
}