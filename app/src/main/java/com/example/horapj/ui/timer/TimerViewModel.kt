package com.example.horapj.ui.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.horapj.data.entity.Company
import com.example.horapj.data.entity.TimeLog
import com.example.horapj.data.repository.CompanyRepository
import com.example.horapj.data.repository.TimeLogRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Enum para os estados do Timer
enum class TimerState {
    STOPPED,
    RUNNING,
    PAUSED
}

// Estado da UI para a tela do Timer
data class TimerUiState(
    val companies: List<Company> = emptyList(),
    val selectedCompany: Company? = null,
    val timerState: TimerState = TimerState.STOPPED,
    val elapsedTimeInMillis: Long = 0L,
    val formattedTime: String = "00:00:00",
    val currentEarnings: Double = 0.0,
    val errorMessage: String? = null
)

class TimerViewModel(
    private val companyRepository: CompanyRepository,
    private val timeLogRepository: TimeLogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0L
    private var pausedTime: Long = 0L // Armazena o tempo acumulado durante as pausas

    init {
        // Combina o Flow de 'allCompanies' com o 'elapsedTimeInMillis'
        viewModelScope.launch {
            companyRepository.allCompanies
                .combine(_uiState) { companies, state ->
                    // Atualiza a lista de empresas e garante que
                    // a empresa selecionada (se houver) esteja atualizada
                    val currentSelected = state.selectedCompany?.let { sel ->
                        companies.find { it.id == sel.id }
                    } ?: companies.firstOrNull() // Seleciona a primeira por padrão

                    // Calcula os ganhos
                    val earnings = currentSelected?.let {
                        val hours = state.elapsedTimeInMillis / 3_600_000.0 // millis para horas
                        hours * it.hourlyRate
                    } ?: 0.0

                    state.copy(
                        companies = companies,
                        selectedCompany = currentSelected,
                        currentEarnings = earnings
                    )
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }

    fun onCompanySelected(company: Company) {
        _uiState.update { it.copy(selectedCompany = company) }
    }

    fun startTimer() {
        if (_uiState.value.selectedCompany == null) {
            _uiState.update { it.copy(errorMessage = "Por favor, selecione uma empresa.") }
            return
        }

        when (_uiState.value.timerState) {
            TimerState.STOPPED -> {
                startTime = System.currentTimeMillis()
                pausedTime = 0L
            }
            TimerState.PAUSED -> {
                // Ao resumir, ajusta o 'startTime' para descontar o tempo pausado
                startTime = System.currentTimeMillis() - pausedTime
            }
            TimerState.RUNNING -> return // Já está rodando
        }

        _uiState.update { it.copy(timerState = TimerState.RUNNING) }

        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                _uiState.update {
                    it.copy(
                        elapsedTimeInMillis = elapsed,
                        formattedTime = formatTime(elapsed)
                    )
                }
                delay(100) // Atualiza a UI 10x por segundo
            }
        }
    }

    fun pauseTimer() {
        if (_uiState.value.timerState != TimerState.RUNNING) return

        timerJob?.cancel() // Para o 'while(true)'
        pausedTime = _uiState.value.elapsedTimeInMillis // Salva o tempo atual
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    fun stopTimer() {
        if (_uiState.value.timerState == TimerState.STOPPED) return

        timerJob?.cancel()
        val endTime = System.currentTimeMillis()

        // Se estava pausado, o 'endTime' precisa ser ajustado
        // para o momento em que a pausa começou
        val actualEndTime = if (_uiState.value.timerState == TimerState.PAUSED) {
            startTime + pausedTime
        } else {
            endTime
        }

        val duration = _uiState.value.elapsedTimeInMillis

        viewModelScope.launch {
            try {
                _uiState.value.selectedCompany?.let {
                    timeLogRepository.insert(
                        TimeLog(
                            companyId = it.id,
                            startTime = startTime,
                            endTime = actualEndTime,
                            durationInMillis = duration
                        )
                    )
                }
                resetTimer()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar: ${e.message}") }
            }
        }
    }

    private fun resetTimer() {
        timerJob?.cancel()
        startTime = 0L
        pausedTime = 0L
        _uiState.update {
            it.copy(
                timerState = TimerState.STOPPED,
                elapsedTimeInMillis = 0L,
                formattedTime = "00:00:00"
            )
        }
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        // Formato HH:MM:SS
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    // Para limpar a mensagem de erro depois que ela for mostrada
    fun onErrorMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}


/**
 * Factory para o TimerViewModel, que precisa de DOIS repositórios.
 */
class TimerViewModelFactory(
    private val companyRepository: CompanyRepository,
    private val timeLogRepository: TimeLogRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(companyRepository, timeLogRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}