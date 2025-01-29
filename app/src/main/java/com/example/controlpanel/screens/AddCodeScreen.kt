package com.example.controlpanel.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

@Composable
fun AddCodeScreen(navController: NavController) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var code by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var numberOfCodes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = numberOfCodes,
            onValueChange = { numberOfCodes = it },
            label = { Text("Number of Codes") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD4AF37),
                unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFFD4AF37),
                unfocusedLabelColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                focusedTextColor = Color(0xFFD4AF37),
                unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it },
            label = { Text("Duration") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFD4AF37),
                unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                focusedLabelColor = Color(0xFFD4AF37),
                unfocusedLabelColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                focusedTextColor = Color(0xFFD4AF37),
                unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (numberOfCodes.isEmpty()) {
                    Toast.makeText(context, "Please fill the number of codes field", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (duration.isEmpty()) {
                    Toast.makeText(context, "Please fill the duration field", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val durationInt = duration.toIntOrNull()
                if (durationInt == null) {
                    Toast.makeText(context, "Invalid duration", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val numberOfCodesInt = numberOfCodes.toIntOrNull()
                if (numberOfCodesInt == null) {
                    Toast.makeText(context, "Invalid number of codes", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                for (i in 1..numberOfCodesInt) {
                    val generatedCode = generateRandomCode()
                    val id = UUID.randomUUID().toString()

                    val codeData = hashMapOf(
                        "id" to id,
                        "duration" to durationInt,
                        "code" to generatedCode,
                        "isUsed" to false,

                    )

                    db.collection("codes")
                        .add(codeData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Code $generatedCode saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error saving code: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                code = ""
                duration = ""
                numberOfCodes = ""
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4AF37),
                contentColor = Color.Black
            )
        ) {
            Text("Save")
        }

        Button(
            onClick = {
                navController.navigate("manage_codes")
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4AF37),
                contentColor = Color.Black
            )
        ) {
            Text("Manage All Codes")
        }
    }
}

fun generateRandomCode(): String {
    val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    val numbers = "0123456789"
    val random = Random()

    val firstPart = (1..3).map { letters[random.nextInt(letters.length)] }.joinToString("")
    val secondPart = (1..9).map { numbers[random.nextInt(numbers.length)] }.joinToString("")

    return firstPart + secondPart
}