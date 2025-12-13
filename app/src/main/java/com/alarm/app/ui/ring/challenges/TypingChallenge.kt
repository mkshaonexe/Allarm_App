package com.alarm.app.ui.ring.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypingChallenge(
    sentences: List<String> = listOf("Typing is fun", "Practice makes perfect"),
    onCompleted: () -> Unit
) {
    var currentSentenceIndex by remember { mutableStateOf(0) }
    
    // Check if we finished all sentences
    LaunchedEffect(currentSentenceIndex) {
        if (currentSentenceIndex >= sentences.size) {
            onCompleted()
        }
    }
    
    if (currentSentenceIndex < sentences.size) {
        val targetPhrase = sentences[currentSentenceIndex]
        var userText by remember(currentSentenceIndex) { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(Unit, currentSentenceIndex) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1C1C1E))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
             // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Handle back */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("${currentSentenceIndex + 1} / ${sentences.size}", color = Color.Gray, fontSize = 16.sp)
                 IconButton(onClick = { /* Toggle Mute */ }) {
                    Icon(Icons.Default.VolumeOff, contentDescription = "Mute", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Text Display Area
            Text(
                text = buildAnnotatedString {
                    // Typed part
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                        append(userText)
                    }
                    // Remaining part (ensure we don't go out of bounds if user typed more than length somehow or just matching prefix)
                    if (userText.length < targetPhrase.length) {
                         // Only append remaining if matches so far? Or just append rest. 
                         // Simple logic: append rest of target
                        withStyle(style = SpanStyle(color = Color.Gray.copy(alpha=0.5f), fontWeight = FontWeight.Bold)) {
                            append(targetPhrase.substring(userText.length))
                        }
                    }
                },
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            // Invisible input field to capture typing
            BasicTextField(
                value = userText,
                onValueChange = { newValue ->
                    // Only update if it matches the prefix of targetPhrase (strict typing) OR just length limit
                    // Let's enforce correct typing for better UX? Or just allow typing.
                    // Strict typing:
                    if (newValue.length <= targetPhrase.length && targetPhrase.startsWith(newValue, ignoreCase = true)) {
                         userText = newValue
                    } else if (newValue.length < userText.length) {
                        // Allow backspace
                        userText = newValue
                    }
                },
                modifier = Modifier
                    .size(1.dp)
                    .focusRequester(focusRequester),
                cursorBrush = SolidColor(Color.Transparent),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Transparent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { 
                     if (userText.equals(targetPhrase, ignoreCase = true)) {
                        currentSentenceIndex++
                     }
                })
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Progress / Count
            Text(
                text = "${userText.length} / ${targetPhrase.length}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Complete Button
            val isComplete = userText.equals(targetPhrase, ignoreCase = true)
            Button(
                onClick = {
                    if (isComplete) {
                        currentSentenceIndex++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isComplete) MaterialTheme.colorScheme.primary else Color(0xFF2C2C2E),
                    contentColor = if (isComplete) Color.White else Color.Gray
                )
            ) {
                Text(if(currentSentenceIndex < sentences.size - 1) "Next" else "Complete", fontSize = 18.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
