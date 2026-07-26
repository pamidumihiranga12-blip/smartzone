package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.model.AppLanguage
import com.example.model.LanguageManager
import com.example.ui.ShopViewModel
import com.example.ui.components.FilterSheet
import com.example.ui.components.SmartZoneHeader
import com.example.ui.screens.*
import com.example.data.FirebaseManager
import com.example.ui.theme.SmartZoneNavy
import com.example.ui.theme.SmartZoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            FirebaseManager.initialize(applicationContext)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        enableEdgeToEdge()
        setContent {
            SmartZoneTheme {
                SmartZoneApp()
            }
        }
    }
}

sealed class Screen(val route: String, val titleKey: String, val icon: ImageVector) {
    object Home : Screen("home", "home", Icons.Default.Home)
    object Shop : Screen("shop", "shop", Icons.Default.Storefront)
    object TrackOrder : Screen("track_order", "track_order", Icons.Default.LocalShipping)
    object Wishlist : Screen("wishlist", "wishlist", Icons.Default.Favorite)
    object Profile : Screen("profile", "profile", Icons.Default.Person)
    object Cart : Screen("cart", "cart", Icons.Default.ShoppingBag)
    object Checkout : Screen("checkout", "checkout", Icons.Default.Lock)
    object ProductDetail : Screen("product_detail", "product_detail", Icons.Default.Info)
    object Notifications : Screen("notifications", "notifications", Icons.Default.Notifications)
    object Admin : Screen("admin", "admin", Icons.Default.AdminPanelSettings)
    object Login : Screen("login", "login", Icons.Default.Login)
}

@Composable
fun SmartZoneApp(viewModel: ShopViewModel = viewModel()) {
    val navController = rememberNavController()

    // ViewModel states
    val products by viewModel.products.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val wishlistIds by viewModel.wishlistProductIds.collectAsStateWithLifecycle()
    val cartWithProducts by viewModel.cartWithProducts.collectAsStateWithLifecycle()
    val cartSubtotal by viewModel.cartSubtotal.collectAsStateWithLifecycle()
    val cartTotal by viewModel.cartTotal.collectAsStateWithLifecycle()
    val discountPercentage by viewModel.discountPercentage.collectAsStateWithLifecycle()
    val orders by viewModel.allOrders.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val siteConfig by viewModel.siteConfig.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val selectedProductReviews by viewModel.selectedProductReviews.collectAsStateWithLifecycle()
    val isAdminMode by viewModel.isAdminMode.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Shop,
        Screen.TrackOrder,
        Screen.Wishlist,
        Screen.Profile
    )

    val wishlistedProducts = remember(products, wishlistIds) {
        products.filter { wishlistIds.contains(it.id) }
    }

    Scaffold(
        topBar = {
            if (currentRoute !in listOf(Screen.Login.route, Screen.Checkout.route, Screen.ProductDetail.route)) {
                SmartZoneHeader(
                    searchQuery = filterState.searchQuery,
                    onSearchChange = {
                        viewModel.updateSearchQuery(it)
                        if (currentRoute != Screen.Shop.route && currentRoute != Screen.Home.route) {
                            navController.navigate(Screen.Shop.route)
                        }
                    },
                    onFilterClick = { showFilterSheet = true },
                    unreadNotificationCount = unreadNotifs,
                    onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                    currentLanguage = currentLanguage,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onCartClick = { navController.navigate(Screen.Cart.route) },
                    cartItemCount = cartWithProducts.sumOf { it.quantity }
                )
            }
        },
        bottomBar = {
            if (currentRoute !in listOf(Screen.Login.route, Screen.Checkout.route)) {
                NavigationBar(
                    containerColor = SmartZoneNavy,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.route
                                )
                            },
                            label = {
                                Text(
                                    text = LanguageManager.getString(screen.titleKey, currentLanguage),
                                    fontSize = 10.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SmartZoneNavy,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        products = products,
                        wishlistIds = wishlistIds,
                        selectedCategory = filterState.selectedCategory,
                        onCategorySelect = { cat ->
                            viewModel.selectCategory(cat)
                            navController.navigate(Screen.Shop.route)
                        },
                        onProductClick = { prod ->
                            viewModel.selectProduct(prod)
                            navController.navigate(Screen.ProductDetail.route)
                        },
                        onAddToCart = { productId -> viewModel.addToCart(productId) },
                        onToggleWishlist = { productId -> viewModel.toggleWishlist(productId) },
                        currentLanguage = currentLanguage,
                        onShopAntennasClick = {
                            viewModel.selectCategory("Accessories")
                            navController.navigate(Screen.Shop.route)
                        },
                        onViewAllClick = {
                            viewModel.resetFilters()
                            navController.navigate(Screen.Shop.route)
                        }
                    )
                }

                composable(Screen.Shop.route) {
                    ShopScreen(
                        products = products,
                        wishlistIds = wishlistIds,
                        filterState = filterState,
                        onCategorySelect = { cat -> viewModel.selectCategory(cat) },
                        onFilterClick = { showFilterSheet = true },
                        onProductClick = { prod ->
                            viewModel.selectProduct(prod)
                            navController.navigate(Screen.ProductDetail.route)
                        },
                        onAddToCart = { productId -> viewModel.addToCart(productId) },
                        onToggleWishlist = { productId -> viewModel.toggleWishlist(productId) },
                        currentLanguage = currentLanguage
                    )
                }

                composable(Screen.ProductDetail.route) {
                    if (selectedProduct == null) {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    } else {
                        selectedProduct?.let { prod ->
                            ProductDetailScreen(
                                product = prod,
                                reviews = selectedProductReviews,
                                isWishlisted = wishlistIds.contains(prod.id),
                                onToggleWishlist = { viewModel.toggleWishlist(prod.id) },
                                onAddToCart = { qty ->
                                    repeat(qty) { viewModel.addToCart(prod.id) }
                                },
                                onBuyNow = { qty ->
                                    repeat(qty) { viewModel.addToCart(prod.id) }
                                    navController.navigate(Screen.Checkout.route)
                                },
                                onSubmitReview = { name, rating, text ->
                                    viewModel.submitReview(prod.id, name, rating, text)
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }

                composable(Screen.Cart.route) {
                    CartScreen(
                        cartItems = cartWithProducts,
                        subtotal = cartSubtotal,
                        total = cartTotal,
                        discountPercentage = discountPercentage,
                        onQuantityChange = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                        onProceedToCheckout = { navController.navigate(Screen.Checkout.route) },
                        onContinueShopping = { navController.navigate(Screen.Shop.route) }
                    )
                }

                composable(Screen.Checkout.route) {
                    CheckoutScreen(
                        totalAmount = cartTotal,
                        userProfile = userProfile,
                        payzyFeePercentage = siteConfig?.payzyFeePercentage ?: 14.0,
                        onPlaceOrder = { method, addr, onSuccess ->
                            viewModel.placeOrder(method, addr, onSuccess)
                        },
                        onBackClick = { navController.popBackStack() },
                        onNavigateToTracking = {
                            navController.navigate(Screen.TrackOrder.route) {
                                popUpTo(Screen.Home.route)
                            }
                        }
                    )
                }

                composable(Screen.TrackOrder.route) {
                    OrderTrackingScreen(
                        orders = orders,
                        onSimulateStatusUpdate = { orderId, status ->
                            viewModel.updateOrderStatus(orderId, status)
                        }
                    )
                }

                composable(Screen.Wishlist.route) {
                    WishlistScreen(
                        wishlistedProducts = wishlistedProducts,
                        onProductClick = { prod ->
                            viewModel.selectProduct(prod)
                            navController.navigate(Screen.ProductDetail.route)
                        },
                        onAddToCart = { id -> viewModel.addToCart(id) },
                        onRemoveFromWishlist = { id -> viewModel.toggleWishlist(id) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        userProfile = userProfile,
                        siteConfig = siteConfig,
                        orders = orders,
                        currentLanguage = currentLanguage,
                        onLanguageSelect = { viewModel.setLanguage(it) },
                        isAdminMode = isAdminMode,
                        onToggleAdminMode = {
                            viewModel.toggleAdminMode()
                        },
                        onTrackOrderClick = {
                            navController.navigate(Screen.TrackOrder.route)
                        },
                        onUpdateProfile = { n, e, p, a ->
                            viewModel.updateProfile(n, e, p, a)
                        },
                        onLoginSuccess = { email, name ->
                            viewModel.loginUser(email, name)
                        },
                        onAddNewProduct = { t, p, c, d, img ->
                            viewModel.addNewProduct(t, p, c, d, img)
                        },
                        onUpdateOrderStatus = { id, status ->
                            viewModel.updateOrderStatus(id, status)
                        },
                        onUpdateSiteSettings = { storeName, storeSubtitle, phone, address, bankName, bankAccNo, bankAccName, merchantId, secretKey, url, key ->
                            viewModel.updateSiteSettings(storeName, storeSubtitle, phone, address, bankName, bankAccNo, bankAccName, merchantId, secretKey, url, key)
                        },
                        onLogout = {
                            viewModel.logoutUser()
                        }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen(
                        notifications = notifications,
                        onNotificationClick = { notif ->
                            viewModel.markNotificationRead(notif.id)
                            notif.orderId?.let {
                                navController.navigate(Screen.TrackOrder.route)
                            }
                        }
                    )
                }

                composable(Screen.Admin.route) {
                    AdminDashboardScreen(
                        orders = orders,
                        siteConfig = siteConfig,
                        onUpdateOrderStatus = { orderId, status ->
                            viewModel.updateOrderStatus(orderId, status)
                        },
                        onAddNewProduct = { title, price, cat, desc, img ->
                            viewModel.addNewProduct(title, price, cat, desc, img)
                        },
                        onUpdateSiteSettings = { storeName, storeSubtitle, phone, address, bankName, bankAccNo, bankAccName, merchantId, secretKey, url, key ->
                            viewModel.updateSiteSettings(storeName, storeSubtitle, phone, address, bankName, bankAccNo, bankAccName, merchantId, secretKey, url, key)
                        },
                        onSyncMySql = { onResult ->
                            viewModel.syncMySqlWebsiteData(onResult)
                        },
                        onLogoutAdmin = {
                            viewModel.logoutUser()
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                composable(Screen.Login.route) {
                    LoginScreen(
                        googleClientId = userProfile?.googleClientId ?: "896110852289-jses5doifgticugc4klracafn2covk7c.apps.googleusercontent.com",
                        onLoginSuccess = { email, name ->
                            viewModel.loginUser(email, name)
                            navController.navigate(Screen.Home.route)
                        },
                        onContinueAsGuest = {
                            viewModel.loginUser("guest@smartzone.lk", "Guest Customer")
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }
            }

            // Filter BottomSheet
            if (showFilterSheet) {
                FilterSheet(
                    filterState = filterState,
                    onFilterStateChange = { newState ->
                        viewModel.updateSearchQuery(newState.searchQuery)
                        viewModel.selectCategory(newState.selectedCategory)
                        viewModel.updatePriceRange(newState.minPrice, newState.maxPrice)
                        viewModel.updateMinRating(newState.minRating)
                        viewModel.toggleOnlySale(newState.onlySale)
                        viewModel.setSortBy(newState.sortBy)
                    },
                    onReset = { viewModel.resetFilters() },
                    onDismiss = { showFilterSheet = false }
                )
            }
        }
    }
}
