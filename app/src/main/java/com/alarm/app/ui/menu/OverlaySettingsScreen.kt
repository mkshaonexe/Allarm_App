package com.alarm.app.ui.menu

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.alarm.app.AlarmApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverlaySettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as AlarmApplication
    val settingsRepository = remember { application.container.settingsRepository }
    
    // State for the current URI
    var overlayUri by remember { mutableStateOf(settingsRepository.getOverlayImageUri()) }
    
    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Take persistable permission
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            
            val uriString = uri.toString()
            overlayUri = uriString
            settingsRepository.saveOverlayImageUri(uriString)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Overlay Settings", 
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Back", 
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
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
            
            // Preview Section
            Text(
                "PREVIEW",
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            
            // Mock Alarm Screen (Miniature)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.6f) // roughly phone aspect ratio
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(24.dp))
                    .background(Color.Black)
            ,
                contentAlignment = Alignment.Center
            ) {
                 Column(
                     horizontalAlignment = Alignment.CenterHorizontally,
                     verticalArrangement = Arrangement.spacedBy(32.dp)
                 ) {
                     // Time
                     Text("10:44", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                     
                     // The Overlay Image (Moon or Custom)
                     Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray)
                            .border(2.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                     ) {
                         if (overlayUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(overlayUri),
                                contentDescription = "Custom Overlay",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                         } else {
                             Text("🌑", fontSize = 100.sp)
                         }
                     }
                     
                     // Fake Snooze
                      Surface(
                        shape = RoundedCornerShape(percent = 50),
                        color = Color.White,
                        modifier = Modifier
                            .height(40.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        ) {
                            Text("Snooze", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                 }
            }
            
            // Actions
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C1C1E)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Select Image from Gallery", color = Color.White, fontSize = 16.sp)
            }
            
             if (overlayUri != null) {
                TextButton(
                    onClick = {
                        overlayUri = null
                        settingsRepository.saveOverlayImageUri(null)
                    }
                ) {
                    Text("Reset to Default Moon", color = Color.Red)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "This image will appear in the center of the screen when your alarm rings.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
