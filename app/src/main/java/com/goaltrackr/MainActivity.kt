package com.goaltrackr

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.goaltrackr.navigation.AppNavGraph
import com.goaltrackr.ui.theme.GoalTrackrTheme
import com.goaltrackr.viewmodel.GoalViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)    // ❌ AnalyticsHelper.init(this) artık gerekmiyor çünkü lazy

        setContent {
            val navController = rememberNavController()
            val isDarkTheme = remember { mutableStateOf(false) }
            val goalViewModel: GoalViewModel = viewModel()

            GoalTrackrTheme(darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        RequestNotificationPermission()
                    }

                    AppNavGraph(
                        navController = navController,
                        isDarkTheme = isDarkTheme.value,
                        onToggleTheme = { isDarkTheme.value = !isDarkTheme.value },
                        onDeleteAllGoals = { goalViewModel.deleteAllGoals() }
                    )
                }
            }
        }
    }
}

@Composable
fun RequestNotificationPermission() {
    val context = LocalContext.current
    val permissionState = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState.value = isGranted
        val message = if (isGranted) "Bildirim izni verildi" else "Bildirim izni reddedildi"
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(Unit) {
        if (!permissionState.value) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}