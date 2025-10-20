package com.bearkicks.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bearkicks.app.features.home.presentation.HomeScreen
import com.bearkicks.app.features.auth.presentation.LoginScreen
import com.bearkicks.app.features.auth.presentation.RegisterScreen
import com.bearkicks.app.features.profile.presentation.ProfileScreen
import com.bearkicks.app.features.shop.presentation.ShoeDetailScreen
import com.bearkicks.app.features.shop.presentation.ShopScreen
import com.bearkicks.app.features.wishlist.presentation.WishListScreen
import com.bearkicks.app.features.cart.presentation.CartScreen
import com.bearkicks.app.features.cart.presentation.OrdersHistoryScreen
import com.bearkicks.app.ui.components.BKBottomBar
import com.bearkicks.app.ui.components.BKTopBar
import org.koin.compose.koinInject
import com.bearkicks.app.features.auth.domain.usecase.ObserveAuthStateUseCase
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val observeAuth: ObserveAuthStateUseCase = koinInject()
    val user by observeAuth().collectAsState(initial = null)

    // If not authenticated, show a dedicated auth graph only (login/register)
    if (user == null) {
        val nav = rememberNavController()
        NavHost(navController = nav, startDestination = Screen.Login.route) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoggedIn = { /* Auth state updates -> recomposes to main graph */ },
                    onGoRegister = { nav.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegistered = { /* Auth state updates -> recomposes to main graph */ },
                    onBackToLogin = { nav.popBackStack() }
                )
            }
        }
        return
    }

    // Authenticated graph with full app UI (top/bottom bars + screens)
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Screen.Home.route

    val bottomRoutes = setOf(
        Screen.Shop.route, Screen.Wishlist.route, Screen.Home.route,
        Screen.Cart.route, Screen.Profile.route
    )
    val showBars = current in bottomRoutes

    Scaffold(
        topBar = {
            if (showBars) {
                val title = when (current) {
                    Screen.Shop.route -> Screen.Shop.title
                    Screen.Wishlist.route -> Screen.Wishlist.title
                    Screen.Home.route -> Screen.Home.title
                    Screen.Cart.route -> Screen.Cart.title
                    Screen.Profile.route -> Screen.Profile.title
                    else -> ""
                }
                BKTopBar(title = title)
            }
        },
        bottomBar = {
            if (showBars) {
                BKBottomBar(
                    currentRoute = current,
                    onItemSelected = { item ->
                        val r = item.route
                        if (r != current) {
                            nav.navigate(r) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Shop.route) {
                ShopScreen(
                    onProductClick = { shoe ->
                        nav.navigate("${Screen.ShoeDetail.route}/${shoe.id}")
                    }
                )
            }
            composable("${Screen.ShoeDetail.route}/{shoeId}") { back ->
                val id = back.arguments?.getString("shoeId") ?: return@composable
                ShoeDetailScreen(
                    shoeId = id,
                    onBack = { nav.popBackStack() },
                    onAddToCartNavigate = {
                        nav.popBackStack()
                        nav.navigate(Screen.Cart.route)
                    },
                    isLoggedIn = true,
                    onRequireLogin = { /* already logged */ }
                )
            }
            composable(Screen.Wishlist.route) {
                WishListScreen(
                    onProductClick = { shoe ->
                        nav.navigate("${Screen.ShoeDetail.route}/${shoe.id}")
                    }
                )
            }
            composable(Screen.Cart.route) {
                CartScreen { _ ->
                    nav.navigate(Screen.Profile.route)
                }
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLoggedOut = { /* When logout occurs, auth state will switch to auth graph */ },
                    onSeeAllOrders = { nav.navigate(Screen.Orders.route) }
                )
            }
            composable(Screen.Orders.route) { OrdersHistoryScreen(onBack = { nav.popBackStack() }) }
        }
    }
}
