package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val title: String,
    val price: Double,
    val originalPrice: Double,
    val category: String,
    val imageUrl: String,
    val rating: Float,
    val reviewCount: Int,
    val isSale: Boolean,
    val description: String,
    val isAvailable: Boolean = true,
    val brand: String = "SmartZone"
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val productId: String,
    val quantity: Int
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val userId: String,
    val itemsSummary: String, // JSON or formatted text
    val totalAmount: Double,
    val paymentMethod: String,
    val deliveryAddress: String,
    val status: String, // PLACED, PROCESSING, SHIPPED, OUT_FOR_DELIVERY, DELIVERED
    val trackingNumber: String,
    val courierName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryDate: String = "2-3 Working Days"
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val userName: String,
    val rating: Float,
    val comment: String,
    val date: String,
    val isVerifiedPurchase: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val orderId: String? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "current_user",
    val name: String = "SmartZone Tech User",
    val email: String = "smartzonelk101@gmail.com",
    val phone: String = "0786800086",
    val address: String = "Anuradhapura",
    val selectedLanguage: String = "EN", // EN, SI, TA
    val googleClientId: String = "896110852289-jses5doifgticugc4klracafn2covk7c.apps.googleusercontent.com",
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)

@Entity(tableName = "site_config")
data class SiteConfigEntity(
    @PrimaryKey val id: String = "main_config",
    val storeName: String = "SMART ZONE",
    val storeSubtitle: String = "Powered by Pamidu",
    val phone: String = "0786800086",
    val address: String = "Anuradhapura",
    val bankName: String = "BOC (Bank of Ceylon)",
    val bankAccNo: String = "90231938",
    val bankAccName: String = "IPMD WIJEGUNAWARDHANA",
    val payzyMerchantId: String = "567",
    val payzySecretKey: String = "0d2a8f76-b73e-461f-b89a-04d73c892f50",
    val payzyFeePercentage: Double = 14.0,
    val noticeBanner: String = "Unlocking Zone & Tech Store Anuradhapura. Free delivery islandwide!",
    val mysqlApiUrl: String = "https://smartzonelk.lk/api",
    val mysqlApiKey: String = "sz_api_key_90231938",
    val isMysqlSyncEnabled: Boolean = true,
    val mysqlDbHost: String = "sdb-l.hosting.stackcp.net",
    val mysqlDbName: String = "smartzoneweb-313932d478",
    val mysqlDbUser: String = "smartzoneweb-313932d478",
    val mysqlDbPass: String = "6EiwvqlQu"
)
