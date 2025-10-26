package com.example.horapj.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.horapj.ui.navigation.Routes
import androidx.compose.material3.OutlinedButton // <-- ADICIONE ESTE IMPORT
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo ao HoraPJ!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- ADICIONE ESTE BOTÃO ---
            Button(
                onClick = {
                    // Navega para a tela de Empresas
                    navController.navigate(Routes.COMPANY)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Gerenciar Empresas")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // --- FIM DA ADIÇÃO ---

            OutlinedButton( // <-- MUDEI O BOTÃO DE SAIR PARA 'Outlined'
                onClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth() // <-- ADICIONEI MODIFIER
            ) {
                Text("Sair")
            }
        }
    }
}