package com.brewmate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brewmate.ui.theme.*
import com.brewmate.viewmodel.BrewViewModel

private enum class AuthMode { SIGN_IN, REGISTER }

@Composable
fun AuthScreen(viewModel: BrewViewModel) {
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }
    var idInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmCream)
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.verticalGradient(listOf(CoffeeBrown, Caramel)),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Coffee,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BrewMate",
            style = MaterialTheme.typography.headlineLarge,
            color = DarkText,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (mode == AuthMode.SIGN_IN) "Sign in with your 5-digit ID" else "New here? Create your profile",
            style = MaterialTheme.typography.bodyMedium,
            color = LightText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (mode == AuthMode.SIGN_IN) {
            OutlinedTextField(
                value = idInput,
                onValueChange = {
                    if (it.length <= 5 && it.all { c -> c.isDigit() }) {
                        idInput = it
                        errorMessage = null
                    }
                },
                label = { Text("5-digit ID") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when (viewModel.signIn(idInput)) {
                        BrewViewModel.SignInResult.Success -> { /* root composable reacts automatically */ }
                        BrewViewModel.SignInResult.InvalidFormat -> errorMessage = "Enter a valid 5-digit ID"
                        BrewViewModel.SignInResult.NotFound -> {
                            errorMessage = null
                            nameInput = ""
                            mode = AuthMode.REGISTER
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown)
            ) {
                Text("Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { viewModel.continueAsGuest() }) {
                Text("Continue as Guest", color = LightText)
            }
        } else {
            // ── REGISTER mode ──────────────────────────────────
            Text(
                text = "ID $idInput isn't registered yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = LightText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it; errorMessage = null },
                label = { Text("Your name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    when (viewModel.register(idInput, nameInput)) {
                        BrewViewModel.RegisterResult.Success -> { /* root composable reacts automatically */ }
                        BrewViewModel.RegisterResult.EmptyName -> errorMessage = "Please enter your name"
                        BrewViewModel.RegisterResult.IdTaken -> errorMessage = "That ID is already registered"
                        BrewViewModel.RegisterResult.InvalidFormat -> errorMessage = "Invalid ID"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CoffeeBrown)
            ) {
                Text("Create Profile & Sign In", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = {
                mode = AuthMode.SIGN_IN
                errorMessage = null
            }) {
                Text("Back to Sign In", color = LightText)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Remember your 5-digit ID — it's both your username and password.",
            style = MaterialTheme.typography.labelSmall,
            color = LightText,
            textAlign = TextAlign.Center
        )
    }
}