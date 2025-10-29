package com.example.horapj.ui.company

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.horapj.data.entity.Company
import com.example.horapj.data.repository.CompanyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine // <-- 1. IMPORTE O 'combine'
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CompanyViewModel(private val repository: CompanyRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CompanyUiState())
    val uiState: StateFlow<CompanyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allCompanies
                .combine(_uiState) { companies, state ->
                    val filteredList = if (state.searchQuery.isBlank()) {
                        companies // Se a busca estiver vazia, mostra tudo
                    } else {
                        companies.filter {
                            it.name.contains(state.searchQuery, ignoreCase = true)
                        }
                    }
                    state.copy(companyList = filteredList)
                }
                .catch { exception ->
                    _uiState.update { it.copy(errorMessage = "Erro ao carregar empresas: ${exception.message}") }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    // --- Funções para atualizar o estado do formulário ---

    fun onCompanyNameChange(name: String) {
        _uiState.update { it.copy(companyName = name, errorMessage = null) }
    }

    fun onHourlyRateChange(rate: String) {
        _uiState.update { it.copy(hourlyRate = rate, errorMessage = null) }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }


    fun loadCompanyForEdit(companyId: Int?) {
        if (companyId == null) {
            _uiState.update {
                it.copy(
                    selectedCompanyId = null,
                    companyName = "",
                    hourlyRate = "",
                    saveSuccess = false,
                    errorMessage = null
                )
            }
        } else {
            viewModelScope.launch {
                val company = repository.getCompanyById(companyId)
                if (company != null) {
                    _uiState.update {
                        it.copy(
                            selectedCompanyId = company.id,
                            companyName = company.name,
                            hourlyRate = company.hourlyRate.toString(),
                            saveSuccess = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "Empresa não encontrada.") }
                }
            }
        }
    }

    fun saveCompany() {
        val name = uiState.value.companyName
        val rate = uiState.value.hourlyRate.toDoubleOrNull()

        if (name.isBlank() || rate == null || rate <= 0) {
            _uiState.update { it.copy(errorMessage = "Nome ou valor por hora inválidos.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        val companyId = uiState.value.selectedCompanyId

        val company = Company(
            id = companyId ?: 0,
            name = name.trim(),
            hourlyRate = rate
        )

        viewModelScope.launch {
            try {
                if (companyId == null) {
                    repository.insert(company)
                } else {
                    repository.update(company)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saveSuccess = true,
                        selectedCompanyId = null,
                        companyName = "",
                        hourlyRate = "",
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    fun deleteCompany(company: Company) {
        viewModelScope.launch {
            try {
                repository.delete(company)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao deletar: ${e.message}") }
            }
        }
    }
}

class CompanyViewModelFactory(private val repository: CompanyRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompanyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompanyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}