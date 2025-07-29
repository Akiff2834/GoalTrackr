@file:OptIn(ExperimentalMaterial3Api::class)

package com.goaltrackr.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goaltrackr.viewmodel.GoalViewModel
import com.google.firebase.auth.FirebaseAuth
import com.goaltrackr.ui.components.CompletionPieChart // 🔹 Bunu eklediğine emin ol

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: GoalViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val goals = viewModel.goals.collectAsState().value
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Hoş geldiniz", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(user?.email ?: "E-posta bulunamadı", fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(24.dp))

            val completed = goals.count { it.isCompleted }
            val uncompleted = goals.count { !it.isCompleted }

            Text("Hedef Sayısı: ${goals.size}", style = MaterialTheme.typography.titleMedium)
            Text("Tamamlanan: $completed")
            Text("Tamamlanmamış: $uncompleted")

            Spacer(modifier = Modifier.height(24.dp))

            CompletionPieChart(completed = completed, uncompleted = uncompleted)

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val email = user?.email
                    if (!email.isNullOrEmpty()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener { task ->
                                val message = if (task.isSuccessful) {
                                    "Şifre sıfırlama bağlantısı gönderildi"
                                } else {
                                    "Şifre sıfırlama başarısız oldu"
                                }
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                    }
                }
            ) {
                Text("Şifreyi Sıfırla")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.errorContainer)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Çıkış Yap")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Çıkış Yap")
            }
        }
    }
}
