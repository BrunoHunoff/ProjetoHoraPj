package com.example.horapj.ui.company.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.horapj.data.entity.Company
import com.example.horapj.data.entity.TimeLog
import com.example.horapj.data.repository.CompanyRepository
import com.example.horapj.data.repository.TimeLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Estado da UI para a tela de Detalhes

class CompanyDetailViewModel(
    private val companyRepository: CompanyRepository,
    private val timeLogRepository: TimeLogRepository,
    private val companyId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyDetailUiState())
    val uiState: StateFlow<CompanyDetailUiState> = _uiState.asStateFlow()

    init {
        loadCompanyDetails()
    }

    private fun loadCompanyDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Busca os dados da empresa (uma única vez)
                val company = companyRepository.getCompanyById(companyId)
                if (company == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Empresa não encontrada.") }
                    return@launch
                }

                // 2. Ouve o Flow de TimeLogs (reativo)
                timeLogRepository.getLogsForCompany(companyId)
                    .catch { e ->
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao buscar logs: ${e.message}") }
                    }
                    .collect { logs ->
                        // 3. Calcula os totais sempre que a lista de logs mudar
                        val totalMillis = logs.sumOf { it.durationInMillis }
                        val totalHoursDecimal = totalMillis / 3_600_000.0 // Apenas para ganhos
                        val totalEarnings = totalHoursDecimal * company.hourlyRate

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                company = company,
                                timeLogs = logs,
                                totalDurationMillis = totalMillis, // <-- CORRIGIDO
                                totalEarnings = totalEarnings
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro: ${e.message}") }
            }
        }
    }
}


/**
 * Factory para o CompanyDetailViewModel.
 * Ela é mais complexa pois agora precisa dos dois repositórios.
 */
class CompanyDetailViewModelFactory(
    private val companyRepository: CompanyRepository,
    private val timeLogRepository: TimeLogRepository
) : ViewModelProvider.Factory {
    // Esta Factory não será usada diretamente por nós, mas pelo
    // Hilt ou um Factory Provider customizado que lide com SavedStateHandle.
    // Por enquanto, vamos criar uma Factory mais simples para o NavHost.
    // Vamos simplificar isso na Parte 12 (Navegação).
}