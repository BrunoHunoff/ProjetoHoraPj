package com.example.horapj.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.horapj.data.dao.AggregatedLogData
import com.example.horapj.data.repository.TimeLogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val aggregatedLogs: List<AggregatedLogData> = emptyList(),
    val totalEarnings: Double = 0.0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DashboardViewModel(private val timeLogRepository: TimeLogRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            timeLogRepository.aggregatedLogs
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Erro: ${e.message}")
                    }
                }
                .collect { logs ->
                    // Calcula o faturamento total
                    val total = logs.sumOf {
                        val hours = it.totalDurationInMillis / 3_600_000.0
                        hours * it.hourlyRate
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            aggregatedLogs = logs,
                            totalEarnings = total
                        )
                    }
                }
        }
    }
}

class DashboardViewModelFactory(
    private val repository: TimeLogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}