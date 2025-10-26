package com.example.horapj.ui.company

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.horapj.data.entity.Company
import java.text.NumberFormat
import java.util.Locale

// Precisamos desta anotação para o TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyScreen(
    viewModel: CompanyViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efeito para mostrar Toasts de erro
    LaunchedEffect(key1 = uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    // Efeito para mostrar Toast de sucesso ao salvar
    LaunchedEffect(key1 = uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Empresa salva com sucesso!", Toast.LENGTH_SHORT).show()
        }
    }

    // Formata o valor para R$
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Empresa") }, // [cite: 2]
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // --- INÍCIO DO FORMULÁRIO  ---
            Text("Qual nome da empresa?", style = MaterialTheme.typography.labelMedium) // [cite: 3]
            OutlinedTextField(
                value = uiState.companyName,
                onValueChange = { viewModel.onCompanyNameChange(it) },
                placeholder = { Text("Empresa...") }, // [cite: 4]
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Quanto você ganha por hora?", style = MaterialTheme.typography.labelMedium) // [cite: 5]
            OutlinedTextField(
                value = uiState.hourlyRate,
                onValueChange = { viewModel.onHourlyRateChange(it) },
                placeholder = { Text("R$...") }, // [cite: 6]
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = { viewModel.saveCompany() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // O botão muda de texto se estivermos editando
                    val buttonText = if (uiState.selectedCompanyId == null) {
                        "Adicionar +" //
                    } else {
                        "Atualizar Empresa"
                    }
                    Text(buttonText)
                }
            }

            // Botão "Cancelar Edição" que só aparece se estivermos editando
            if (uiState.selectedCompanyId != null) {
                Button(
                    onClick = { viewModel.loadCompanyForEdit(null) }, // Limpa o formulário
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar Edição")
                }
            }
            // --- FIM DO FORMULÁRIO ---

            Divider(modifier = Modifier.padding(vertical = 16.dp))

            // --- INÍCIO DA BUSCA E LISTAGEM  ---
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Busque empresas já cadastradas...") }, //
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Cabeçalho da Lista
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("ID EMPRESA", style = MaterialTheme.typography.labelSmall) // [cite: 9]
                Text("AÇÕES", style = MaterialTheme.typography.labelSmall) // [cite: 10]
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Empresas
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.companyList, key = { it.id }) { company ->
                    CompanyListItem(
                        company = company,
                        formattedRate = currencyFormatter.format(company.hourlyRate),
                        onEditClicked = {
                            // Ao clicar em Editar, o ViewModel carrega os dados
                            // da empresa no formulário lá em cima.
                            viewModel.loadCompanyForEdit(company.id)
                        },
                        onDeleteClicked = {
                            viewModel.deleteCompany(company)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Um Composable separado para cada item da lista de empresas.
 */
@Composable
fun CompanyListItem(
    company: Company,
    formattedRate: String,
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ID e Nome [cite: 9, 11, 12]
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${company.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = company.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = formattedRate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Ações (Editar/Deletar) [cite: 10]
            Row {
                IconButton(onClick = onEditClicked) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Deletar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}