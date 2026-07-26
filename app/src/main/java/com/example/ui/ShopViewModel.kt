package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.*
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = ShopRepository(db.shopDao())

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // Filter State
    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    // Current App Language
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    // Products Flow
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .combine(_filterState) { rawList, filter ->
            rawList.filter { product ->
                val matchesSearch = filter.searchQuery.isEmpty() ||
                        product.title.contains(filter.searchQuery, ignoreCase = true) ||
                        product.category.contains(filter.searchQuery, ignoreCase = true) ||
                        product.description.contains(filter.searchQuery, ignoreCase = true)

                val matchesCategory = filter.selectedCategory == "All" ||
                        product.category.equals(filter.selectedCategory, ignoreCase = true)

                val matchesPrice = product.price in filter.minPrice..filter.maxPrice
                val matchesRating = product.rating >= filter.minRating
                val matchesSale = !filter.onlySale || product.isSale

                matchesSearch && matchesCategory && matchesPrice && matchesRating && matchesSale
            }.let { filtered ->
                when (filter.sortBy) {
                    SortOption.POPULARITY -> filtered.sortedByDescending { it.reviewCount }
                    SortOption.PRICE_LOW_HIGH -> filtered.sortedBy { it.price }
                    SortOption.PRICE_HIGH_LOW -> filtered.sortedByDescending { it.price }
                    SortOption.RATING -> filtered.sortedByDescending { it.rating }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Wishlist Product IDs
    val wishlistProductIds: StateFlow<Set<String>> = repository.wishlistItems
        .map { items -> items.map { it.productId }.toSet() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // Cart Items with Details
    val cartWithProducts: StateFlow<List<CartItemWithProduct>> = combine(
        repository.allProducts,
        repository.cartItems
    ) { allProductsList, cartEntities ->
        val productMap = allProductsList.associateBy { it.id }
        cartEntities.mapNotNull { cartItem ->
            productMap[cartItem.productId]?.let { product ->
                CartItemWithProduct(product, cartItem.quantity)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Promo Coupon state
    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon.asStateFlow()

    val discountPercentage: StateFlow<Double> = _appliedCoupon.map { coupon ->
        if (coupon?.uppercase() == "SMART10") 0.10 else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Total Cart Value
    val cartSubtotal: StateFlow<Double> = cartWithProducts.map { items ->
        items.sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotal: StateFlow<Double> = combine(cartSubtotal, discountPercentage) { sub, disc ->
        val discounted = sub * (1.0 - disc)
        val shipping = if (sub > 10000.0 || sub == 0.0) 0.0 else 350.0
        discounted + shipping
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Orders Flow
    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // User Profile
    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Site Configuration Flow
    val siteConfig: StateFlow<SiteConfigEntity?> = repository.siteConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Auth functions
    fun loginUser(email: String, name: String, isAdmin: Boolean = false) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            val isUserAdmin = isAdmin || email.lowercase().contains("admin") || email.equals("smartzonelk101@gmail.com", ignoreCase = true)
            repository.updateProfile(
                current.copy(
                    email = email,
                    name = if (name.isBlank()) "SmartZone User" else name,
                    isLoggedIn = true,
                    isAdmin = isUserAdmin
                )
            )
            _isAdminMode.value = isUserAdmin
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(
                    isLoggedIn = false,
                    isAdmin = false
                )
            )
            _isAdminMode.value = false
        }
    }

    fun updateSiteSettings(
        storeName: String,
        storeSubtitle: String,
        phone: String,
        address: String,
        bankName: String,
        bankAccNo: String,
        bankAccName: String,
        merchantId: String,
        secretKey: String,
        mysqlApiUrl: String = "https://smartzone.lk/api",
        mysqlApiKey: String = "sz_api_key_90231938"
    ) {
        viewModelScope.launch {
            val current = siteConfig.value ?: SiteConfigEntity()
            val updated = current.copy(
                storeName = storeName,
                storeSubtitle = storeSubtitle,
                phone = phone,
                address = address,
                bankName = bankName,
                bankAccNo = bankAccNo,
                bankAccName = bankAccName,
                payzyMerchantId = merchantId,
                payzySecretKey = secretKey,
                mysqlApiUrl = mysqlApiUrl,
                mysqlApiKey = mysqlApiKey
            )
            repository.updateSiteConfig(updated)
        }
    }

    fun syncMySqlWebsiteData(onResult: (Int) -> Unit = {}) {
        viewModelScope.launch {
            val config = siteConfig.value
            val url = config?.mysqlApiUrl ?: "https://smartzone.lk/api"
            val key = config?.mysqlApiKey ?: "sz_api_key_90231938"
            val count = repository.syncWithMySqlDatabase(url, key)
            onResult(count)
        }
    }

    // Notifications
    val notifications: StateFlow<List<NotificationEntity>> = repository.notifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val unreadNotificationCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Selected Product for Detail View
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    // Reviews for Selected Product
    val selectedProductReviews: StateFlow<List<ReviewEntity>> = _selectedProduct.flatMapLatest { prod ->
        if (prod != null) repository.getReviews(prod.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Mode state
    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    fun toggleAdminMode() {
        _isAdminMode.value = !_isAdminMode.value
    }

    // Filter Actions
    fun updateSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: String) {
        _filterState.value = _filterState.value.copy(selectedCategory = category)
    }

    fun updatePriceRange(min: Double, max: Double) {
        _filterState.value = _filterState.value.copy(minPrice = min, maxPrice = max)
    }

    fun updateMinRating(rating: Float) {
        _filterState.value = _filterState.value.copy(minRating = rating)
    }

    fun toggleOnlySale(saleOnly: Boolean) {
        _filterState.value = _filterState.value.copy(onlySale = saleOnly)
    }

    fun setSortBy(sortOption: SortOption) {
        _filterState.value = _filterState.value.copy(sortBy = sortOption)
    }

    fun resetFilters() {
        _filterState.value = FilterState()
    }

    // Language switch
    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        viewModelScope.launch {
            userProfile.value?.let { prof ->
                repository.updateProfile(prof.copy(selectedLanguage = language.code))
            }
        }
    }

    // Product detail selection
    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
    }

    // Cart Actions
    fun addToCart(productId: String) {
        viewModelScope.launch {
            val currentCart = cartWithProducts.value
            val existing = currentCart.find { it.product.id == productId }
            if (existing != null) {
                repository.updateCartQuantity(productId, existing.quantity + 1)
            } else {
                repository.addToCart(productId)
            }
        }
    }

    fun updateCartQuantity(productId: String, qty: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(productId, qty)
        }
    }

    fun applyCoupon(code: String): Boolean {
        return if (code.trim().equals("SMART10", ignoreCase = true)) {
            _appliedCoupon.value = "SMART10"
            true
        } else {
            false
        }
    }

    // Wishlist Action
    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            val isWishlisted = wishlistProductIds.value.contains(productId)
            repository.toggleWishlist(productId, isWishlisted)
        }
    }

    // Place Order
    fun placeOrder(
        paymentMethod: String,
        address: String,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val items = cartWithProducts.value
            if (items.isEmpty()) return@launch

            val orderId = "#SZ-${(10000..99999).random()}"
            val itemsSummary = items.joinToString(", ") { "${it.quantity}x ${it.product.title}" }
            val total = cartTotal.value

            val newOrder = OrderEntity(
                orderId = orderId,
                userId = "current_user",
                itemsSummary = itemsSummary,
                totalAmount = total,
                paymentMethod = paymentMethod,
                deliveryAddress = address,
                status = "PLACED",
                trackingNumber = "TRK-${orderId.replace("#", "")}-SLP",
                courierName = "PromptX Express Courier",
                createdAt = System.currentTimeMillis(),
                estimatedDeliveryDate = "2-3 Working Days"
            )

            repository.placeOrder(newOrder)
            _appliedCoupon.value = null
            onSuccess(orderId)
        }
    }

    // Admin order update
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    // Add Review
    fun submitReview(productId: String, userName: String, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.addReview(productId, userName, rating, comment)
        }
    }

    // Add New Product (Admin)
    fun addNewProduct(
        title: String,
        price: Double,
        category: String,
        description: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            val newId = "prod_${System.currentTimeMillis()}"
            val product = ProductEntity(
                id = newId,
                title = title,
                price = price,
                originalPrice = price * 1.15,
                category = category,
                imageUrl = if (imageUrl.isBlank()) "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=500" else imageUrl,
                rating = 5.0f,
                reviewCount = 1,
                isSale = true,
                description = description
            )
            repository.addProduct(product)
        }
    }

    // Update Profile
    fun updateProfile(name: String, email: String, phone: String, address: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfileEntity()
            repository.updateProfile(
                current.copy(
                    name = name,
                    email = email,
                    phone = phone,
                    address = address
                )
            )
        }
    }

    // Notifications
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            db.shopDao().markNotificationAsRead(id)
        }
    }
}
