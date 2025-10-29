package com.example.horapj.ui.timer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.horapj.data.entity.Company
import com.example.horapj.ui.theme.MainBlue
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TimerScreen(
    viewModel: TimerViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Formata o valor para R$
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    // Efeito para mostrar erros (ex: "Selecione uma empresa")
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onErrorMessageShown() // Limpa o erro
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Topo: Seletor de Empresa e Ganhos ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompanySelector(
                    companies = uiState.companies,
                    selectedCompany = uiState.selectedCompany,
                    onCompanySelected = { viewModel.onCompanySelected(it) },
                    // Desabilita o dropdown se o timer estiver rodando
                    enabled = uiState.timerState == TimerState.STOPPED
                )

                EarningsDisplay(
                    amount = uiState.currentEarnings,
                    formatter = currencyFormatter
                )
            }

            // --- Centro: Timer e Título ---
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Registrar Hora",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = uiState.formattedTime,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            }

            // --- Fundo: Botões de Controle ---
            TimerControls(
                timerState = uiState.timerState,
                onStart = { viewModel.startTimer() },
                onPause = { viewModel.pauseTimer() },
                onStop = { viewModel.stopTimer() }
            )
        }
    }
}

@Composable
fun CompanySelector(
    companies: List<Company>,
    selectedCompany: Company?,
    onCompanySelected: (Company) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(MainBlue)
                .clickable(enabled = enabled) { expanded = true }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Business,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = selectedCompany?.name ?: "Selecione",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            companies.forEach { company ->
                DropdownMenuItem(
                    text = { Text(company.name) },
                    onClick = {
                        onCompanySelected(company)
                        expanded = false
                    }
                )
            }
            if (companies.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Nenhuma empresa cadastrada") },
                    onClick = { expanded = false },
                    enabled = false
                )
            }
        }
    }
}

@Composable
fun EarningsDisplay(amount: Double, formatter: NumberFormat) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(MainBlue)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Wallet,
            contentDescription = null,
            tint = Color.White
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = formatter.format(amount),
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TimerControls(
    timerState: TimerState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (timerState == TimerState.STOPPED) {
            Spacer(modifier = Modifier.size(64.dp))
        } else {
            // Botão de Pausa (fica "invisível" se não estiver pausado)
            Spacer(
                modifier = Modifier
                    .size(64.dp)
                // Truque: o botão de pausa na verdade é o botão central.
                // O botão de Stop fica aqui.
            )
        }
        IconButton(
            onClick = {
                if (timerState == TimerState.RUNNING) onPause() else onStart()
            },
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MainBlue)
        ) {
            Icon(
                imageVector = if (timerState == TimerState.RUNNING) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = if (timerState == TimerState.RUNNING) "Pausar" else "Iniciar",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        if (timerState == TimerState.STOPPED) {
            // Deixa um espaço vazio para centralizar o Play
            Spacer(modifier = Modifier.size(64.dp))
        } else {
            // Botão de Stop
            IconButton(
                onClick = onStop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MainBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Parar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}