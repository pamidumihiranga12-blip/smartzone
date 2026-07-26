package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ProductEntity
import com.example.model.AppLanguage
import com.example.model.LanguageManager
import com.example.ui.components.HeroBanner
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopCategoryRow
import com.example.ui.theme.LightBackground
import com.example.ui.theme.SmartZoneBlue
import com.example.ui.theme.SmartZoneNavy
import com.example.ui.theme.TextMuted

@Composable
fun HomeScreen(
    products: List<ProductEntity>,
    wishlistIds: Set<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
    currentLanguage: AppLanguage,
    onShopAntennasClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 8.dp)
            .testTag("home_screen_grid")
    ) {
        // Hero Banner Item (Spans 2 columns)
        item(span = { GridItemSpan(2) }) {
            HeroBanner(
                currentLanguage = currentLanguage,
                onShopAntennasClick = onShopAntennasClick
            )
        }

        // Shop Categories Row (Spans 2 columns)
        item(span = { GridItemSpan(2) }) {
            ShopCategoryRow(
                selectedCategory = selectedCategory,
                onCategorySelect = onCategorySelect
            )
        }

        // Section Title (Spans 2 columns)
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LanguageManager.getString("latest_products", currentLanguage),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = SmartZoneNavy,
                    letterSpacing = 0.5.sp
                )
                TextButton(
                    onClick = onViewAllClick,
                    modifier = Modifier.testTag("view_all_products_btn")
                ) {
                    Text(
                        text = "View All →",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SmartZoneBlue
                    )
                }
            }
        }

        // Product Cards Grid
        if (products.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found in this category.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            }
        } else {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    isWishlisted = wishlistIds.contains(product.id),
                    onWishlistToggle = { onToggleWishlist(product.id) },
                    onProductClick = { onProductClick(product) },
                    onAddToCartClick = { onAddToCart(product.id) }
                )
            }
        }
    }
}
