package com.goaltrackr.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onDeleteAllGoals: () -> Unit,
    navController: NavController // 👈 eklendi
) {
    val context = LocalContext.current
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ayarlar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Profil", style = MaterialTheme.typography.titleLarge)
            Text("Email: ${user?.email ?: "Giriş yapılmamış"}")

            Divider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Karanlık Tema")
                Switch(checked = isDarkTheme, onCheckedChange = { onToggleTheme() })
            }

            Divider()

            Button(
                onClick = {
                    onDeleteAllGoals()
                    Toast.makeText(context, "Tüm hedefler silindi", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Tüm Hedefleri Sil", color = MaterialTheme.colorScheme.onError)
            }

            OutlinedButton(
                onClick = {
                    FirebaseCrashlytics.getInstance().log("Simulated crash triggered from SettingsScreen")
                    FirebaseCrashlytics.getInstance().recordException(RuntimeException("Test crash"))
                    throw RuntimeException("Simulated Crash for testing Crashlytics")
                }
            ) {
                Text("Simulate Crash")
            }

            OutlinedButton(onClick = onLogout) {
                Text("Çıkış Yap")
            }

            // ✅ Public API Todo Listesi Butonu
            OutlinedButton(onClick = {
                navController.navigate("publicTodos")
            }) {
                Text("Public Todo Listesi")
            }
        }
    }
}
