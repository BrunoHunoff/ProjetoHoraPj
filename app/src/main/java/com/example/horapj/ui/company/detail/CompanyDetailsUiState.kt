package com.example.horapj.ui.company.detail

import com.example.horapj.data.entity.Company
import com.example.horapj.data.entity.TimeLog

data class CompanyDetailUiState(
    val company: Company? = null,
    val timeLogs: List<TimeLog> = emptyList(),
    val totalDurationMillis: Long = 0L, // Antes era: totalHours: Double = 0.0
    val totalEarnings: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)