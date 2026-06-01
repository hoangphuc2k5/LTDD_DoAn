package com.example.tegram.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tegram.domain.model.UserProfile
import com.example.tegram.domain.model.learning.DailyPlan
import com.example.tegram.presentation.home.HomeScreen
import com.example.tegram.presentation.profile.ProfileScreen
import com.example.tegram.ui.theme.TegramDarkBlue
import com.example.tegram.ui.theme.TegramLightBlue

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home_tab", "Trang chủ", Icons.Default.Home)
    object Vocabulary : BottomNavItem("vocabulary_tab", "Từ vựng", Icons.Default.Book)
    object Progress : BottomNavItem("progress_tab", "Thống kê", Icons.Default.BarChart)
    object Profile : BottomNavItem("profile_tab", "Hồ sơ", Icons.Default.Person)
}

@Composable
fun MainScreen(
    user: UserProfile?,
    dailyPlan: DailyPlan,
    onNavigateToFlashcards: () -> Unit,
    onNavigateToReview: () -> Unit,
    onNavigateToDailyPlan: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Vocabulary,
        BottomNavItem.Progress,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = TegramDarkBlue,
                contentColor = Color.White
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = TegramLightBlue
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    user = user,
                    dailyPlan = dailyPlan,
                    onOpenProfile = { navController.navigate(BottomNavItem.Profile.route) },
                    onOpenFlashcards = onNavigateToFlashcards,
                    onOpenReview = onNavigateToReview,
                    onOpenDailyPlan = onNavigateToDailyPlan,
                    onLogout = onLogout
                )
            }
            composable(BottomNavItem.Vocabulary.route) {
                PlaceholderScreen("Quản lý từ vựng (Task Nhựt Hào)")
            }
            composable(BottomNavItem.Progress.route) {
                PlaceholderScreen("Thống kê & Tiến độ (Task 4)")
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    user = user,
                    onBack = { navController.popBackStack() },
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(TegramDarkBlue),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, style = MaterialTheme.typography.headlineSmall)
    }
}
