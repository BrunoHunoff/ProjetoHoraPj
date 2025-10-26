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

    // --- Funções para atualizar o estado a partir da UI ---

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    // <-- ADICIONE ESTA NOVA FUNÇÃO -->
    fun onCpfChange(cpf: String) {
        _uiState.update { it.copy(cpf = cpf, errorMessage = null) }
    }

    // --- Funções de Lógica de Negócio (Ações do Usuário) ---

    fun login() {
        // ... (função login() continua igual à anterior)
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

    // <-- ATUALIZE A FUNÇÃO register() -->
    fun register() {
        // Validação (agora inclui CPF)
        if (uiState.value.email.isBlank() || uiState.value.password.isBlank() || uiState.value.cpf.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos os campos são obrigatórios.") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Adiciona o CPF ao criar o novo usuário
            val newUser = User(
                email = uiState.value.email.trim(),
                password = uiState.value.password,
                cpf = uiState.value.cpf.trim() // <-- ADICIONADO
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

// A Factory (AuthViewModelFactory) continua exatamente igual
class AuthViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    // ... (código da factory sem alterações)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}