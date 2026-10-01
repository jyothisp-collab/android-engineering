package com.example.androidnativeshowcase.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidnativeshowcase.features.auth.LoginRoute
import com.example.androidnativeshowcase.features.home.HomeRoute
import com.example.androidnativeshowcase.features.items.ItemDetailRoute
import com.example.androidnativeshowcase.features.items.ItemsRoute

@Composable
fun ShowcaseApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.Login) {
        composable(Routes.Login) {
            LoginRoute(
                onSignedIn = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.Home) {
            HomeRoute(
                onOpenItems = { navController.navigate(Routes.Items) },
            )
        }
        composable(Routes.Items) {
            ItemsRoute(
                onBack = { navController.popBackStack() },
                onOpenDetail = { id -> navController.navigate(Routes.detail(id)) },
            )
        }
        composable(
            route = Routes.Detail,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType }),
        ) {
            ItemDetailRoute(onBack = { navController.popBackStack() })
        }
    }
}

object Routes {
    const val Login = "login"
    const val Home = "home"
    const val Items = "items"
    const val Detail = "items/{itemId}"

    fun detail(itemId: String) = "items/$itemId"
}
