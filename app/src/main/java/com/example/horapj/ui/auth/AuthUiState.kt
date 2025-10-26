package com.example.horapj.ui.auth

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val cpf: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val registrationSuccess: Boolean = false
)