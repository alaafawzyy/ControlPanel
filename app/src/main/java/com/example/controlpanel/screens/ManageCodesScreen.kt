package com.example.controlpanel.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Timestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ManageCodesScreen(navController: NavController) {
    val context = LocalContext.current
    val db = Firebase.firestore


    var totalCodes by remember { mutableStateOf(0) }
    var unusedCodes by remember { mutableStateOf(0) }
    var codesList by remember { mutableStateOf<List<Code>>(emptyList()) }


    LaunchedEffect(Unit) {
        db.collection("codes")
            .get()
            .addOnSuccessListener { result ->
                codesList = result.map { document ->
                    document.toObject(Code::class.java).copy(
                        id = document.id,
                        isUsed = document.getBoolean("isUsed") ?: null,
                        endDate = document.getTimestamp("endDate") ?: null
                    )
                }
                totalCodes = codesList.size
                unusedCodes = codesList.count { it.isUsed != true }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background((Color.Black))
            .padding(top = 40.dp, bottom = 20.dp)
    ) {

        Text(
            text = "Total Codes: $totalCodes",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp)

        )
        Spacer(modifier = Modifier.height(8.dp))


        Text(
            text = "unused Codes: $unusedCodes",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn {
            items(
                items = codesList,
                key = { it.id } // استخدام id كمعرف فريد
            ) { code ->
                EditableCodeItem(
                    code = code,
                    onSave = { updatedCode ->
                        // الحصول على المرجع مباشرة باستخدام Document ID
                        val documentRef = db.collection("codes").document(updatedCode.id)

                        documentRef.set(
                            mapOf(
                                "code" to updatedCode.code,
                                "duration" to updatedCode.duration,
                                "isUsed" to updatedCode.isUsed,
                                "endDate" to updatedCode.endDate
                            ),
                            SetOptions.merge()
                        )
                            .addOnSuccessListener {
                                Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    },
                    onDelete = {
                        // الحصول على المرجع مباشرة باستخدام Document ID
                        val documentRef = db.collection("codes").document(code.id)

                        documentRef.delete()
                            .addOnSuccessListener {
                                codesList = codesList.filter { it.id != code.id }
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }


@Composable
fun EditableCodeItem(
    code: Code,
    onSave: (Code) -> Unit,
    onDelete: () -> Unit // دالة جديدة للحذف
) {
    var codeText by remember { mutableStateOf(code.code) }
    var durationText by remember { mutableStateOf(code.duration.toString()) }
    var isUsed by remember { mutableStateOf(code.isUsed ?: false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            OutlinedTextField(
                value = codeText,
                onValueChange = { codeText = it },
                label = { Text("Code") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD4AF37),
                    unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                    focusedTextColor = Color(0xFFD4AF37),
                    unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))


            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it },
                label = { Text("Duration (in days)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD4AF37),
                    unfocusedBorderColor = Color(0xFFD4AF37).copy(alpha = 0.5f),
                    focusedTextColor = Color(0xFFD4AF37),
                    unfocusedTextColor = Color(0xFFD4AF37).copy(alpha = 0.8f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Is Used:", color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when (code.isUsed) {
                        true -> "Yes"
                        false -> "No"
                        null -> "Not set"
                    },
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // عرض EndDate

            Text(
                text = "End Date: ${timestampToString(code.endDate)}",
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            // زر Save
            Button(
                onClick = {
                    val durationInt = durationText.toIntOrNull()
                    if (durationInt != null) {
                        val updatedCode = code.copy(
                            code = codeText,
                            duration = durationInt,
                            isUsed = isUsed
                        )
                        onSave(updatedCode)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD4AF37), // لون ذهبي
                    contentColor = Color.Black // لون النص
                )
            ) {
                Text("Save Changes")
            }

            // زر Delete
            Button(
                onClick = {
                    onDelete() // استدعاء دالة الحذف
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red, // لون أحمر
                    contentColor = Color.Black // لون النص
                )
            ) {
                Text("Delete")
            }
        }
    }
}
fun timestampToString(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(it)
    } ?: "Not set"
}


data class Code(
    val id: String = "",
    val code: String = "",
    val duration: Int = 0,
    val isUsed: Boolean? = null,
    val endDate: Timestamp? = null
)