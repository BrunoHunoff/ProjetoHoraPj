package com.example.horapj.ui.auth

// ... (imports)
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.horapj.data.entity.User
import com.example.horapj.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onCpfChange(cpf: String) {
        _uiState.update { it.copy(cpf = cpf, errorMessage = null) }
    }


    fun login() {
        if (uiState.value.email.isBlank() || uiState.value.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email e senha são obrigatórios.") }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val user = userRepository.login(uiState.value.email, uiState.value.password)
            if (user != null) {
                _uiState.update {
                    it.copy(isLoading = false, loginSuccess = true, errorMessage = null)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Email ou senha inválidos.")
                }
            }
        }
    }

    fun register() {
        if (uiState.value.email.isBlank() || uiState.value.password.isBlank() || uiState.value.cpf.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos os campos são obrigatórios.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val newUser = User(
                email = uiState.value.email.trim(),
                password = uiState.value.password,
                cpf = uiState.value.cpf.trim()
            )
            val success = userRepository.register(newUser)

            if (success) {
                _uiState.update {
                    it.copy(isLoading = false, registrationSuccess = true, errorMessage = null)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Este email ou CPF já está cadastrado.")
                }
            }
        }
    }

    fun onNavigationDone() {
        _uiState.update {
            it.copy(loginSuccess = false, registrationSuccess = false, errorMessage = null)
        }
    }
}

class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}