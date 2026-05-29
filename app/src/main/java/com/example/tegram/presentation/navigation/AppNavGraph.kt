package com.example.tegram.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tegram.presentation.auth.AuthViewModel
import com.example.tegram.presentation.auth.login.LoginScreen
import com.example.tegram.presentation.auth.register.RegisterScreen
import com.example.tegram.presentation.home.HomeScreen
import com.example.tegram.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch

private object Routes {
	const val Login = "login"
	const val Register = "register"
	const val Home = "home"
	const val Profile = "profile"
}

@Composable
fun AppNavGraph(
	authViewModel: AuthViewModel = hiltViewModel()
) {
	val navController = rememberNavController()
	val coroutineScope = rememberCoroutineScope()
	val currentUser by authViewModel.currentUser.collectAsState()

	NavHost(
		navController = navController,
		startDestination = Routes.Login
	) {
		composable(Routes.Login) {
			LoginScreen(
				onLogin = { email, password -> authViewModel.loginWithEmail(email, password) },
				onGoogleLogin = { fullName, email, photoUrl -> authViewModel.loginWithGoogle(fullName, email, photoUrl) },
				onAuthSuccess = {
					navController.navigate(Routes.Home) {
						popUpTo(Routes.Login)
						launchSingleTop = true
					}
				},
				onNavigateRegister = { navController.navigate(Routes.Register) }
			)
		}

		composable(Routes.Register) {
			RegisterScreen(
				onRegister = { fullName, email, password ->
					authViewModel.registerWithEmail(fullName, email, password)
				},
				onNavigateLogin = {
					navController.popBackStack(Routes.Login, inclusive = false)
				}
			)
		}

		composable(Routes.Home) {
			HomeScreen(
				user = currentUser,
				onOpenProfile = { navController.navigate(Routes.Profile) },
				onLogout = {
					coroutineScope.launch {
						authViewModel.logout()
						navController.navigate(Routes.Login) {
							popUpTo(Routes.Home)
							launchSingleTop = true
						}
					}
				}
			)
		}

		composable(Routes.Profile) {
			ProfileScreen(
				user = currentUser,
				onBack = { navController.popBackStack() },
				onLogout = {
					coroutineScope.launch {
						authViewModel.logout()
						navController.navigate(Routes.Login) {
							popUpTo(Routes.Home)
							launchSingleTop = true
						}
					}
				}
			)
		}
	}
}
