package com.example.horapj.ui.company

import com.example.horapj.data.entity.Company

data class CompanyUiState(
    val companyList: List<Company> = emptyList(),

    val companyName: String = "",
    val hourlyRate: String = "",

    val searchQuery: String = "",

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCompanyId: Int? = null,
    val saveSuccess: Boolean = false
)