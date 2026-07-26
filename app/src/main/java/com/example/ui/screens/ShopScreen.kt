package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ProductEntity
import com.example.model.AppLanguage
import com.example.model.FilterState
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopCategoryRow
import com.example.ui.theme.LightBackground
import com.example.ui.theme.SmartZoneBlue
import com.example.ui.theme.SmartZoneNavy
import com.example.ui.theme.TextMuted

@Composable
fun ShopScreen(
    products: List<ProductEntity>,
    wishlistIds: Set<String>,
    filterState: FilterState,
    onCategorySelect: (String) -> Unit,
    onFilterClick: () -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (String) -> Unit,
    onToggleWishlist: (String) -> Unit,
    currentLanguage: AppLanguage
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
            .testTag("shop_screen_grid")
    ) {
        // Category Pills Header
        item(span = { GridItemSpan(2) }) {
            ShopCategoryRow(
                selectedCategory = filterState.selectedCategory,
                onCategorySelect = onCategorySelect
            )
        }

        // Active Filter Summary & Trigger
        item(span = { GridItemSpan(2) }) {
            Card(
                shape = MaterialTheme.shapes.small,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Showing ${products.size} Items",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmartZoneNavy
                        )
                        Text(
                            text = "Category: ${filterState.selectedCategory} • Sort: ${filterState.sortBy.title}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    OutlinedButton(
                        onClick = onFilterClick,
                        shape = MaterialTheme.shapes.extraLarge,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("shop_filter_sheet_btn")
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filters", fontSize = 12.sp, color = SmartZoneBlue)
                    }
                }
            }
        }

        // Product Cards Grid
        if (products.isEmpty()) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products match your search or price filters.",
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
