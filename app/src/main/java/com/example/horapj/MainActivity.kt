package com.example.horapj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.horapj.ui.auth.AuthViewModel
import com.example.horapj.ui.auth.AuthViewModelFactory
import com.example.horapj.ui.auth.LoginScreen
import com.example.horapj.ui.auth.RegistrationScreen
import com.example.horapj.ui.home.HomeScreen
import com.example.horapj.ui.navigation.Routes
import com.example.horapj.ui.theme.HoraPJTheme
import com.example.horapj.ui.company.CompanyScreen
import com.example.horapj.ui.company.CompanyViewModel
import com.example.horapj.ui.company.CompanyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoraPJTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicia o AppNavigator
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val application = LocalContext.current.applicationContext as HoraPjApplication

    // 1. Instancia o AuthViewModel (como antes)
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application.userRepository)
    )

    // 2. ADICIONE A INSTÂNCIA DO COMPANY VIEW MODEL
    val companyViewModel: CompanyViewModel = viewModel(
        factory = CompanyViewModelFactory(application.companyRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // Telas de Autenticação (como antes)
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.REGISTRATION) {
            RegistrationScreen(
                viewModel = authViewModel,
                navController = navController
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController
            )
        }

        // 3. ADICIONE O NOVO COMPOSABLE DA TELA DE EMPRESA
        composable(Routes.COMPANY) {
            CompanyScreen(
                viewModel = companyViewModel,
                navController = navController
            )
        }
    }
}

