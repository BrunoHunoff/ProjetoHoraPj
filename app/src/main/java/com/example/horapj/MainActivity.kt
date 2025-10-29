package com.example.horapj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.horapj.ui.auth.AuthViewModel
import com.example.horapj.ui.auth.AuthViewModelFactory
import com.example.horapj.ui.auth.LoginScreen
import com.example.horapj.ui.auth.RegistrationScreen
import com.example.horapj.ui.company.CompanyViewModel
import com.example.horapj.ui.company.CompanyViewModelFactory
import com.example.horapj.ui.company.detail.CompanyDetailScreen
import com.example.horapj.ui.company.detail.CompanyDetailViewModel
import com.example.horapj.ui.main.MainScreen
import com.example.horapj.ui.navigation.Routes
import com.example.horapj.ui.theme.HoraPJTheme
import com.seuprojeto.ui.onboarding.OnboardingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            HoraPJTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val mainNavController = rememberNavController()
    val application = LocalContext.current.applicationContext as HoraPjApplication

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(application.userRepository)
    )
    val companyViewModel: CompanyViewModel = viewModel(
        factory = CompanyViewModelFactory(application.companyRepository)
    )

    NavHost(
        navController = mainNavController,
        startDestination = Routes.ONBOARDING
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onOnboardingFinished = {
                    mainNavController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.ONBOARDING) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                navController = mainNavController
            )
        }
        composable(Routes.REGISTRATION) {
            RegistrationScreen(
                viewModel = authViewModel,
                navController = mainNavController
            )
        }

        composable(Routes.MAIN) {
            MainScreen(
                mainNavController = mainNavController,
                companyViewModel = companyViewModel
            )
        }

        composable(
            route = Routes.COMPANY_DETAIL,
            arguments = listOf(navArgument("companyId") { type = NavType.IntType })
        ) { backStackEntry ->

            val companyIdFromArgs = backStackEntry.arguments?.getInt("companyId")

            if (companyIdFromArgs == null) {
                Text("Erro: ID da empresa não encontrado.", modifier = Modifier.padding(16.dp))
                return@composable
            }

            val detailViewModel: CompanyDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(CompanyDetailViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return CompanyDetailViewModel(
                                companyRepository = application.companyRepository,
                                timeLogRepository = application.timeLogRepository,
                                companyId = companyIdFromArgs // <-- A correção está aqui
                            ) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )

            CompanyDetailScreen(
                viewModel = detailViewModel,
                navController = mainNavController
            )
        }
    }
}