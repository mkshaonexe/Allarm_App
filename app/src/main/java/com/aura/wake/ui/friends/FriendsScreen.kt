package com.aura.wake.ui.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aura.wake.data.model.Profile
import com.aura.wake.data.repository.AuthRepository
import com.aura.wake.data.repository.FriendRepository
import com.aura.wake.ui.components.LoginPrompt
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(navController: NavController) {
    val authRepository = remember { AuthRepository() }
    val friendRepository = remember { FriendRepository() }
    val sessionStatus by authRepository.sessionStatus.collectAsState(initial = SessionStatus.NotAuthenticated(false))
    val scope = rememberCoroutineScope()
    
    var friends by remember { mutableStateOf<List<Profile>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(sessionStatus) {
        if (sessionStatus is SessionStatus.Authenticated) {
            isLoading = true
            friends = friendRepository.getFriends()
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (sessionStatus) {
            is SessionStatus.Authenticated -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        "Friends",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFFFF9800))
                        }
                    } else if (friends.isEmpty()) {
                        // Empty State
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 64.dp), // Space for FAB
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SentimentDissatisfied,
                                contentDescription = null,
                                tint = Color.DarkGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "No friends yet",
                                color = Color.Gray,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Add friends to see them here!",
                                color = Color.Gray.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // Space for FAB
                        ) {
                            items(friends) { friend ->
                                FriendItem(friend)
                            }
                        }
                    }
                }

                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFFFF9800), // Dayline Orange
                    contentColor = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Friend")
                }
            }
            else -> {
                LoginPrompt(onLoginSuccess = {})
            }
        }

        if (showAddDialog) {
            AddFriendDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { email ->
                    scope.launch {
                        isLoading = true
                        val success = friendRepository.addFriendByEmail(email)
                        if (success) {
                            friends = friendRepository.getFriends()
                            showAddDialog = false
                        } else {
                            // Show error toast or snackbar (omitted for brevity)
                        }
                        isLoading = false
                    }
                }
            )
        }
    }
}

@Composable
fun FriendItem(profile: Profile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF424242), Color(0xFF212121))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = (profile.username?.take(1) ?: "?").uppercase(),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = profile.username ?: "Unknown",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = profile.email ?: "",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun AddFriendDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    
    AlertDialog(
        containerColor = Color(0xFF1E1E1E),
        onDismissRequest = onDismiss,
        title = { Text("Add Friend", color = Color.White) },
        text = {
            Column {
                Text(
                    "Enter your friend's email address to add them.",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFFF9800),
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(email) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}
