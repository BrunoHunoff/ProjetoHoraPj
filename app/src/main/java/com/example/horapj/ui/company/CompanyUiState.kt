package com.example.horapj.ui.company

import com.example.horapj.data.entity.Company

data class CompanyUiState(
    val companyList: List<Company> = emptyList(),

    // Campo para o formulário
    val companyName: String = "",
    val hourlyRate: String = "",

    // Campo para a busca
    val searchQuery: String = "", // <-- ADICIONE ESTA LINHA

    // Controle de estado
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCompanyId: Int? = null,
    val saveSuccess: Boolean = false
)