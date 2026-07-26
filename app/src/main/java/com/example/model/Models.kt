package com.example.model

data class FilterState(
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val minPrice: Double = 0.0,
    val maxPrice: Double = 50000.0,
    val minRating: Float = 0.0f,
    val onlySale: Boolean = false,
    val sortBy: SortOption = SortOption.POPULARITY
)

enum class SortOption(val title: String) {
    POPULARITY("Most Popular"),
    PRICE_LOW_HIGH("Price: Low to High"),
    PRICE_HIGH_LOW("Price: High to Low"),
    RATING("Highest Rated")
}

data class CartItemWithProduct(
    val product: com.example.data.db.ProductEntity,
    val quantity: Int
)

data class OrderStatusStep(
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val timestamp: String
)
