package com.example.horapj.ui.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.horapj.ui.navigation.Routes

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efeito para navegar quando o login for bem-sucedido
    LaunchedEffect(key1 = uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.LOGIN) { inclusive = true }
            }
            viewModel.onNavigationDone()
        }
    }

    // Efeito para mostrar Toasts de erro
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- Textos do seu design ---
            Text(
                text = "Bem-vindo!", // [cite: 15]
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Olá! Por favor, faça login primeiro", // [cite: 16]
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- Campos do seu design ---
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") }, // [cite: 17]
                placeholder = { Text("Digite seu email...") }, // [cite: 18]
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Senha") }, // [cite: 19]
                placeholder = { Text("Digite sua senha...") }, // [cite: 20]
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { /* Lógica de "Esqueci a senha" (não implementada) */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Esqueceu sua senha?") // [cite: 23]
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- Botões do seu design ---
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.login() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logar") // [cite: 21]
                }
            }

            TextButton(
                onClick = {
                    navController.navigate(Routes.REGISTRATION)
                }
            ) {
                Text("Ainda não tem uma conta? Cadastre-se") // [cite: 24]
            }
        }
    }
}