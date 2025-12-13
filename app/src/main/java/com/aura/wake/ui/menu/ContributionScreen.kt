package com.aura.wake.ui.menu

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController

data class Contributor(
    val name: String,
    val date: String,
    val description: String,
    val role: String? = null // For special roles like "(HTT Engineer, SQA)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionScreen(navController: NavController) {
    val context = LocalContext.current
    var showInfoDialog by remember { mutableStateOf(false) }

    // Sample Data (Currently Empty)
    val contributors = listOf<Contributor>()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Contribution",
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
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:mkshaonnew31@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "AuraWake Bug Report")
                            val body = """
                                App: AuraWake
                                
                                What happened:
                                [Describe the bug here]
                                
                                If possible, please:
                                1. Screen record the issue
                                2. Upload the video to your YouTube (unlisted or public)
                                3. Copy the link and paste it here
                                
                                YouTube Video Link:
                                
                                Thank you! ❤️
                            """.trimIndent()
                            putExtra(Intent.EXTRA_TEXT, body)
                        }
                        try {
                            context.startActivity(emailIntent)
                        } catch (e: Exception) {
                            // Handle case where no email app is available
                            android.widget.Toast.makeText(context, "No email app found", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C3CC)), // Cyan color
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        "Report a Bug",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "Bug Reporters & Contributors",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            if (contributors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No bugs reported yet.\nBe the first to contribute!",
                        color = Color.DarkGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(contributors) { contributor ->
                        ContributorCard(contributor)
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        Dialog(onDismissRequest = { showInfoDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "How to get listed?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        "If you find a bug in Social Sentry, report it to us!\n\n" +
                        "Once our team verifies and fixes the bug you reported, your name will be added to this Contribution list as a token of our appreciation.\n❤️",
                        fontSize = 16.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(
                        onClick = { showInfoDialog = false }
                    ) {
                        Text("Got it", color = Color(0xFF00C3CC), fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ContributorCard(contributor: Contributor) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        contributor.name,
                        color = Color(0xFF00C3CC), // Cyan name
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    if (contributor.role != null) {
                        Text(
                            contributor.role,
                            color = Color(0xFF00C3CC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                Text(
                    contributor.date,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                contributor.description,
                color = Color.LightGray,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}
