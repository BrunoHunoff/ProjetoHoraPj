package com.example.horapj.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.horapj.HoraPjApplication
import com.example.horapj.ui.company.CompanyScreen
import com.example.horapj.ui.company.CompanyViewModel
import com.example.horapj.ui.dashboard.DashboardScreen
import com.example.horapj.ui.dashboard.DashboardViewModel
import com.example.horapj.ui.dashboard.DashboardViewModelFactory
import com.example.horapj.ui.navigation.BottomNavItem
import com.example.horapj.ui.profile.ProfileScreen
import com.example.horapj.ui.theme.MainBlue
import com.example.horapj.ui.timer.TimerScreen
import com.example.horapj.ui.timer.TimerViewModel
import com.example.horapj.ui.timer.TimerViewModelFactory

@Composable
fun MainScreen(
    mainNavController: NavController,
    companyViewModel: CompanyViewModel
) {
    val nestedNavController = rememberNavController()
    val application = LocalContext.current.applicationContext as HoraPjApplication

    val timerViewModel: TimerViewModel = viewModel(
        factory = TimerViewModelFactory(
            application.companyRepository,
            application.timeLogRepository
        )
    )
    val dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModelFactory(application.timeLogRepository)
    )

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                navController = nestedNavController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Timer.route) {
                TimerScreen(viewModel = timerViewModel)
            }
            composable(BottomNavItem.Home.route) {
                DashboardScreen(
                    viewModel = dashboardViewModel,
                    navController = mainNavController
                )
            }
            composable(BottomNavItem.Companies.route) {
                CompanyScreen(
                    viewModel = companyViewModel,
                    navController = mainNavController
                )
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(mainNavController = mainNavController)
            }

        }
    }
}

@Composable
private fun CustomBottomBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.Timer,
        BottomNavItem.Home,
        BottomNavItem.Companies,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomAppBar(
        modifier = Modifier.height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp))

            BottomBarItem(
                item = BottomNavItem.Timer,
                isSelected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.Timer.route } == true,
                onClick = { navigateTo(navController, BottomNavItem.Timer.route) },
            )

            FabBottomBarItem(
                item = BottomNavItem.Home,
                isSelected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.Home.route } == true,
                onClick = { navigateTo(navController, BottomNavItem.Home.route) },
            )

            BottomBarItem(
                item = BottomNavItem.Companies,
                isSelected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.Companies.route } == true,
                onClick = { navigateTo(navController, BottomNavItem.Companies.route) }
            )

            BottomBarItem(
                item = BottomNavItem.Profile,
                isSelected = currentDestination?.hierarchy?.any { it.route == BottomNavItem.Profile.route } == true,
                onClick = { navigateTo(navController, BottomNavItem.Profile.route) }
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MainBlue

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun FabBottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val fabColor = MainBlue
    val iconColor = Color.White

    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(fabColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}