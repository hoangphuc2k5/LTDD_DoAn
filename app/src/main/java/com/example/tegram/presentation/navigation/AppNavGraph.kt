package com.example.tegram.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.tegram.presentation.learning.LearningViewModel
import com.example.tegram.presentation.learning.dailyplan.DailyPlanScreen
import com.example.tegram.presentation.learning.flashcard.FlashcardScreen
import com.example.tegram.presentation.learning.review.SrsReviewScreen
import com.example.tegram.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch

private object Routes {
	const val Login = "login"
	const val Register = "register"
	const val Home = "home"
	const val Profile = "profile"
	const val Flashcards = "flashcards"
	const val Review = "review"
	const val DailyPlan = "dailyPlan"
}

@Composable
fun AppNavGraph(
	authViewModel: AuthViewModel = hiltViewModel(),
	learningViewModel: LearningViewModel = hiltViewModel()
) {
	val navController = rememberNavController()
	val coroutineScope = rememberCoroutineScope()
	val currentUser by authViewModel.currentUser.collectAsState()
	val learningState by learningViewModel.uiState.collectAsState()

	LaunchedEffect(currentUser?.uid) {
		learningViewModel.load(currentUser?.uid)
	}

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
				dailyPlan = learningState.dailyPlan,
				onOpenProfile = { navController.navigate(Routes.Profile) },
				onOpenFlashcards = { navController.navigate(Routes.Flashcards) },
				onOpenReview = { navController.navigate(Routes.Review) },
				onOpenDailyPlan = { navController.navigate(Routes.DailyPlan) },
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

		composable(Routes.Flashcards) {
			FlashcardScreen(
				cards = learningState.cards,
				isLoading = learningState.isLoading,
				errorMessage = learningState.errorMessage,
				onRefresh = learningViewModel::refresh,
				onSeed = { learningViewModel.seed(force = true) },
				onCreateFlashcard = learningViewModel::createFlashcard,
				onDeleteFlashcard = learningViewModel::deleteFlashcard,
				onBack = { navController.popBackStack() },
				onStartReview = { navController.navigate(Routes.Review) }
			)
		}

		composable(Routes.Review) {
			SrsReviewScreen(
				cards = learningState.cards,
				schedules = learningState.schedules,
				isLoading = learningState.isLoading,
				errorMessage = learningState.errorMessage,
				onRefresh = learningViewModel::refresh,
				onRate = learningViewModel::submitReview,
				onBack = { navController.popBackStack() },
				onOpenDailyPlan = { navController.navigate(Routes.DailyPlan) }
			)
		}

		composable(Routes.DailyPlan) {
			DailyPlanScreen(
				plan = learningState.dailyPlan,
				cards = learningState.cards,
				schedules = learningState.schedules,
				isLoading = learningState.isLoading,
				errorMessage = learningState.errorMessage,
				onRefresh = learningViewModel::refresh,
				onSeed = { learningViewModel.seed(force = true) },
				onBack = { navController.popBackStack() },
				onStartFlashcards = { navController.navigate(Routes.Flashcards) },
				onStartReview = { navController.navigate(Routes.Review) }
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
