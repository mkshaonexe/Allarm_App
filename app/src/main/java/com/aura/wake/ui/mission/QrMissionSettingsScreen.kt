package com.aura.wake.ui.mission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aura.wake.ui.AppViewModelProvider
import android.Manifest
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QrMissionSettingsScreen(
    navController: NavController,
    viewModel: MissionSettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var qrContent by remember { mutableStateOf(viewModel.qrContent) }
    var qrLabel by remember { mutableStateOf(viewModel.qrLabel) }
    var isScanning by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }

    if (showPermissionRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionRationaleDialog = false },
            title = { Text("Camera Permission Required") },
            text = { Text("Camera access is needed to scan QR codes for this mission.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionRationaleDialog = false
                        val intent = android.content.Intent(
                            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        ).apply {
                            data = android.net.Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LaunchedEffect(viewModel.qrContent, viewModel.qrLabel) {
        qrContent = viewModel.qrContent
        qrLabel = viewModel.qrLabel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Mission", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Check if scanning
            if (isScanning) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF6EC3F5))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Simulating Scan...", color = Color.White)
                    }
                }
            } else {
                // Display Current QR
                Card(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.QrCode, 
                                contentDescription = null, 
                                tint = if (qrContent != null) Color(0xFF6EC3F5) else Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = qrLabel ?: "No QR Code Saved",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                            if (qrContent != null) {
                                Text(
                                    text = "Content: $qrContent",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Button(
                    onClick = { 
                        if (cameraPermissionState.status.isGranted) {
                            // Permission granted: Simulate Scan
                            isScanning = true
                            scope.launch {
                                delay(2000) // Simulate camera delay
                                val newContent = "simulated_qr_code_${System.currentTimeMillis()}"
                                val newLabel = "My Toothpaste" // Mock label
                                viewModel.saveQrSettings(newContent, newLabel)
                                isScanning = false
                            }
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6EC3F5))
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (cameraPermissionState.status.isGranted) "Scan New QR Code" else "Grant Camera Permission to Scan", fontSize = 16.sp)
                }

                if (!cameraPermissionState.status.isGranted) {
                     TextButton(onClick = { showPermissionRationaleDialog = true }) {
                         Text("Permission Issues? Open Settings", color = Color.Gray, fontSize = 12.sp)
                     }
                }

                if (qrContent != null) {
                    TextButton(
                        onClick = { viewModel.saveQrSettings("", "") } // Clear
                    ) {
                        Text("Clear Saved QR", color = Color.Red)
                    }
                }
                
                Text(
                    "Note: Actual camera scanning will be implemented in the next phase. This currently simulates a successful scan.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
