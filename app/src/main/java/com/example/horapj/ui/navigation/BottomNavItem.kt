package com.example.horapj.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime // Ícone para o Timer
import androidx.compose.material.icons.filled.Apartment // Ícone para Empresas (do seu código)
import androidx.compose.material.icons.filled.Home // Ícone para o Botão Central
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Define as 4 telas principais da nossa navegação
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        route = "dashboard",
        title = "Home",
        icon = Icons.Default.Home
    )

    data object Timer : BottomNavItem(
        route = "timer",
        title = "Timer",
        icon = Icons.Default.AccessTime
    )

    data object Companies : BottomNavItem(
        route = "company",
        title = "Empresas",
        icon = Icons.Default.Apartment
    )

    data object Profile : BottomNavItem(
        route = "profile",
        title = "Perfil",
        icon = Icons.Default.Person
    )
}