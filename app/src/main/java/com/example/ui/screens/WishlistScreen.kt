package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.LightBackground
import com.example.ui.theme.SaleRed
import com.example.ui.theme.SmartZoneNavy
import com.example.ui.theme.TextMuted

@Composable
fun WishlistScreen(
    wishlistedProducts: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    onAddToCart: (String) -> Unit,
    onRemoveFromWishlist: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(12.dp)
            .testTag("wishlist_screen")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = SaleRed)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Saved Wishlist (${wishlistedProducts.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = SmartZoneNavy
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (wishlistedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Wishlist is Empty", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Tap the heart icon on any router or accessory to save it here.", fontSize = 12.sp, color = TextMuted)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(wishlistedProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        isWishlisted = true,
                        onWishlistToggle = { onRemoveFromWishlist(product.id) },
                        onProductClick = { onProductClick(product) },
                        onAddToCartClick = { onAddToCart(product.id) }
                    )
                }
            }
        }
    }
}
