package com.example.data.db

import com.example.data.FirebaseManager
import kotlinx.coroutines.flow.Flow

class ShopRepository(private val dao: ShopDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val wishlistItems: Flow<List<WishlistItemEntity>> = dao.getWishlistItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()
    val siteConfig: Flow<SiteConfigEntity?> = dao.getSiteConfig()
    val notifications: Flow<List<NotificationEntity>> = dao.getNotifications()

    suspend fun seedInitialDataIfEmpty() {
        // Seed site config
        val initialConfig = SiteConfigEntity(
            id = "main_config",
            storeName = "SMART ZONE",
            storeSubtitle = "Powered by Pamidu",
            phone = "0786800086",
            address = "Anuradhapura",
            bankName = "BOC (Bank of Ceylon)",
            bankAccNo = "90231938",
            bankAccName = "IPMD WIJEGUNAWARDHANA",
            payzyMerchantId = "567",
            payzySecretKey = "0d2a8f76-b73e-461f-b89a-04d73c892f50",
            payzyFeePercentage = 14.0,
            noticeBanner = "Unlocking Zone & Tech Store Anuradhapura. Phone: 078 68 000 86"
        )
        dao.updateSiteConfig(initialConfig)
        FirebaseManager.syncSiteConfig(
            initialConfig.storeName,
            initialConfig.storeSubtitle,
            initialConfig.phone,
            initialConfig.address,
            initialConfig.bankName,
            initialConfig.bankAccNo,
            initialConfig.bankAccName,
            initialConfig.payzyMerchantId,
            initialConfig.payzySecretKey
        )

        // Seed default profile
        dao.updateUserProfile(
            UserProfileEntity(
                id = "current_user",
                name = "SmartZone Member",
                email = "smartzonelk101@gmail.com",
                phone = "0786800086",
                address = "Anuradhapura",
                selectedLanguage = "EN",
                googleClientId = "896110852289-jses5doifgticugc4klracafn2covk7c.apps.googleusercontent.com",
                isLoggedIn = false,
                isAdmin = false
            )
        )

        // Seed initial notifications
        dao.insertNotification(
            NotificationEntity(
                title = "Welcome to SmartZone!",
                message = "Get 4G & 5G Routers, Antennas, and Unlock Services with PayZy 4-installment checkout.",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = false
            )
        )

        // Seed products matching reference image
        val defaultProducts = listOf(
            ProductEntity(
                id = "slt_s12pro",
                title = "SLT S12pro brand new router",
                price = 14999.0,
                originalPrice = 17349.0,
                category = "Routers",
                imageUrl = "https://images.unsplash.com/photo-1544197150-b99a580bb7a8?w=500",
                rating = 5.0f,
                reviewCount = 727,
                isSale = true,
                description = "Brand new SLT S12pro 4G/5G Router. High speed dual band WiFi 6 with external antenna ports and Gigabit Ethernet."
            ),
            ProductEntity(
                id = "slt_s10",
                title = "SLT S10 BRAND NEW UNLOCK ROUTER",
                price = 8990.0,
                originalPrice = 10330.0,
                category = "Routers",
                imageUrl = "https://images.unsplash.com/photo-1606904825846-647eb07f5be2?w=500",
                rating = 4.9f,
                reviewCount = 442,
                isSale = true,
                description = "Unlocked SLT S10 4G LTE Router works with any SIM card (Dialog, Mobitel, Hutch, Airtel). Plug and play broadband."
            ),
            ProductEntity(
                id = "dialog_s12pro_unlock",
                title = "Dialog S12Pro Router unlock service [Remortly unlock via Anydesk]",
                price = 850.0,
                originalPrice = 978.0,
                category = "Router Unlock Service",
                imageUrl = "https://images.unsplash.com/photo-1563770660941-20978e870e26?w=500",
                rating = 4.8f,
                reviewCount = 523,
                isSale = true,
                description = "Instant remote unlock service for Dialog S12Pro Router via AnyDesk. Safe, permanent SIM unlock for all Sri Lanka networks."
            ),
            ProductEntity(
                id = "unlock_tool_rent",
                title = "Unlock Tool Rent | 06 Hours | Source 01",
                price = 301.0,
                originalPrice = 346.0,
                category = "Unlock Tools Rent",
                imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
                rating = 4.7f,
                reviewCount = 173,
                isSale = true,
                description = "Rent professional router unlock tool access for 06 hours. Instant login delivery upon checkout."
            ),
            ProductEntity(
                id = "dialog_s50_unlock",
                title = "Dialog S50 Router unlock service online [Unlock via Anydesk]",
                price = 999.0,
                originalPrice = 1149.0,
                category = "Router Unlock Service",
                imageUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=500",
                rating = 4.9f,
                reviewCount = 142,
                isSale = true,
                description = "Online remote unlocking for Dialog S50 routers. Fully guaranteed service by SmartZone technicians."
            ),
            ProductEntity(
                id = "dialog_tv_remote",
                title = "DIALOG TV HD REMOTE",
                price = 1200.0,
                originalPrice = 1380.0,
                category = "Remotes",
                imageUrl = "https://images.unsplash.com/photo-1522869635100-9f4c5e86aa37?w=500",
                rating = 4.8f,
                reviewCount = 180,
                isSale = true,
                description = "Original Dialog TV HD Set Top Box replacement remote controller. Pre-programmed and ready to use."
            ),
            ProductEntity(
                id = "zlt_s50_unlocked",
                title = "ZLT S50 UNLOCKED Brand New Routers",
                price = 7500.0,
                originalPrice = 9825.0,
                category = "Routers",
                imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=500",
                rating = 5.0f,
                reviewCount = 1000,
                isSale = true,
                description = "ZLT S50 4G+ Unlocked Router with all SIM card support. Strong signal reception and built-in battery backup support."
            ),
            ProductEntity(
                id = "slt_s10_unlock",
                title = "SLT S10 ROUTER UNLOCK SERVISE [Remortly unlock via Anydesk]",
                price = 500.0,
                originalPrice = 575.0,
                category = "Router Unlock Service",
                imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
                rating = 4.9f,
                reviewCount = 886,
                isSale = true,
                description = "Quick remote unlock for SLT S10 routers via AnyDesk. Fast 10-minute setup."
            ),
            ProductEntity(
                id = "borneo_schematics",
                title = "Borneo Schematics Tool Rent | 30 Minuts | 09.00 - 23.00 (Sri Lanka)",
                price = 250.0,
                originalPrice = 300.0,
                category = "Unlock Tools Rent",
                imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=500",
                rating = 4.6f,
                reviewCount = 95,
                isSale = true,
                description = "Borneo Schematics tool rent access for hardware technician diagnostics and PCB repair diagrams."
            ),
            ProductEntity(
                id = "high_gain_yagi",
                title = "Boost 4G & 5G High-Gain Outdoor Yagi Antenna 28dBi",
                price = 12500.0,
                originalPrice = 15000.0,
                category = "Accessories",
                imageUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500",
                rating = 5.0f,
                reviewCount = 310,
                isSale = true,
                description = "High-Gain 28dBi Yagi & Omni-Directional 4G/5G outdoor signal boosting antenna with 10m low-loss SMA coaxial cable."
            ),
            ProductEntity(
                id = "wholesale_pack",
                title = "Wholesale Unlocked 4G Routers (5 Units Bundle)",
                price = 37500.0,
                originalPrice = 45000.0,
                category = "Wholesale Rates",
                imageUrl = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=500",
                rating = 4.9f,
                reviewCount = 64,
                isSale = true,
                description = "Bulk wholesale offer for tech resellers: 5 units of factory unlocked 4G LTE routers at wholesale rates."
            )
        )

        dao.insertProducts(defaultProducts)

        // Seed reviews
        val sampleReviews = listOf(
            ReviewEntity(
                productId = "zlt_s50_unlocked",
                userName = "Nuwan Perera",
                rating = 5.0f,
                comment = "Patta router eka! Dialog and Mobitel both working fast in Kandy signal area. Fast delivery by SmartZone!",
                date = "2026-07-20"
            ),
            ReviewEntity(
                productId = "zlt_s50_unlocked",
                userName = "Kasun Fernando",
                rating = 5.0f,
                comment = "Very good product. Unlocked as described. PayZy 4 installment feature was super helpful!",
                date = "2026-07-18"
            ),
            ReviewEntity(
                productId = "slt_s12pro",
                userName = "Dinesh Silva",
                rating = 5.0f,
                comment = "Awesome speed! SmartZone team unlocked and configured external antenna port for me.",
                date = "2026-07-22"
            )
        )
        sampleReviews.forEach { dao.insertReview(it) }

        // Seed sample order for tracking demo
        val sampleOrder = OrderEntity(
            orderId = "#SZ-98421",
            userId = "current_user",
            itemsSummary = "1x ZLT S50 UNLOCKED Brand New Routers, 1x DIALOG TV HD REMOTE",
            totalAmount = 8700.0,
            paymentMethod = "PayZy (4x Rs 2,175.00)",
            deliveryAddress = "No. 78, Station Road, Colombo 04, Sri Lanka",
            status = "SHIPPED",
            trackingNumber = "TRK-98421-SLP",
            courierName = "PromptX Courier Sri Lanka",
            createdAt = System.currentTimeMillis() - 86400000,
            estimatedDeliveryDate = "Tomorrow, 2:00 PM"
        )
        dao.insertOrder(sampleOrder)
    }

    suspend fun getProduct(id: String) = dao.getProductById(id)

    suspend fun addToCart(productId: String) {
        val existing = dao.getCartItems()
        // We handle logic in repository or viewmodel
        dao.insertCartItem(CartItemEntity(productId = productId, quantity = 1))
    }

    suspend fun updateCartQuantity(productId: String, qty: Int) {
        if (qty <= 0) {
            dao.removeCartItem(productId)
        } else {
            dao.insertCartItem(CartItemEntity(productId = productId, quantity = qty))
        }
    }

    suspend fun toggleWishlist(productId: String, isWishlisted: Boolean) {
        if (isWishlisted) {
            dao.removeWishlist(productId)
        } else {
            dao.insertWishlist(WishlistItemEntity(productId = productId))
        }
    }

    suspend fun placeOrder(order: OrderEntity) {
        dao.insertOrder(order)
        dao.clearCart()
        FirebaseManager.syncOrder(order.orderId, order.totalAmount, order.paymentMethod, order.deliveryAddress, order.status)
        
        // Sync to Website MySQL API (Triggers automatic order insertion and Email sending to User + Admin)
        val config = dao.getSiteConfigDirect()
        val url = config?.mysqlApiUrl ?: "https://smartzonelk.lk/api"
        val key = config?.mysqlApiKey ?: "sz_api_key_90231938"
        com.example.data.MySqlApiManager.syncOrderToWebsite(url, key, order)

        // Create notification
        dao.insertNotification(
            NotificationEntity(
                title = "Order Placed Successfully! (${order.orderId})",
                message = "Your order for Rs ${String.format("%.2f", order.totalAmount)} has been received and is being processed.",
                orderId = order.orderId
            )
        )
    }

    suspend fun updateOrderStatus(orderId: String, status: String) {
        dao.updateOrderStatus(orderId, status)
        dao.insertNotification(
            NotificationEntity(
                title = "Order Status Update ($orderId)",
                message = "Your order status is now: $status. Tracking No: TRK-${orderId.replace("#", "")}",
                orderId = orderId
            )
        )
    }

    fun getReviews(productId: String) = dao.getReviewsForProduct(productId)

    suspend fun addReview(productId: String, userName: String, rating: Float, comment: String) {
        dao.insertReview(
            ReviewEntity(
                productId = productId,
                userName = userName,
                rating = rating,
                comment = comment,
                date = "Today"
            )
        )
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        dao.updateUserProfile(profile)
    }

    suspend fun addProduct(product: ProductEntity) {
        dao.insertProduct(product)
        FirebaseManager.syncProduct(product.id, product.title, product.price, product.category, product.description, product.imageUrl)
        
        // Sync new product to Website MySQL DB
        val config = dao.getSiteConfigDirect()
        val url = config?.mysqlApiUrl ?: "https://smartzonelk.lk/api"
        val key = config?.mysqlApiKey ?: "sz_api_key_90231938"
        com.example.data.MySqlApiManager.syncProductToWebsite(url, key, product)
    }

    suspend fun updateSiteConfig(config: SiteConfigEntity) {
        dao.updateSiteConfig(config)
        FirebaseManager.syncSiteConfig(
            config.storeName,
            config.storeSubtitle,
            config.phone,
            config.address,
            config.bankName,
            config.bankAccNo,
            config.bankAccName,
            config.payzyMerchantId,
            config.payzySecretKey
        )
    }

    suspend fun syncWithMySqlDatabase(apiUrl: String, apiKey: String): Int {
        val remoteProducts = com.example.data.MySqlApiManager.fetchProductsFromWebsite(apiUrl, apiKey)
        for (p in remoteProducts) {
            dao.insertProduct(p)
        }
        return remoteProducts.size
    }
}
