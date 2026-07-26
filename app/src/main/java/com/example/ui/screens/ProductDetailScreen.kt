package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.ProductEntity
import com.example.data.db.ReviewEntity
import com.example.ui.theme.*

@Composable
fun ProductDetailScreen(
    product: ProductEntity,
    reviews: List<ReviewEntity>,
    isWishlisted: Boolean,
    onToggleWishlist: () -> Unit,
    onAddToCart: (Int) -> Unit,
    onBuyNow: (Int) -> Unit,
    onSubmitReview: (String, Float, String) -> Unit,
    onBackClick: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(1) }
    var showReviewDialog by remember { mutableStateOf(false) }

    val payzyInstallment = product.price / 4.0

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightBackground)
                            .padding(4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = quantity.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Add to Cart
                    OutlinedButton(
                        onClick = { onAddToCart(quantity) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_btn")
                    ) {
                        Text("Add to Cart", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Buy Now
                    Button(
                        onClick = { onBuyNow(quantity) },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_buy_now_btn")
                    ) {
                        Text("Buy Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(LightBackground)
                .testTag("product_detail_screen")
        ) {
            // Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.White)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(Color.White, CircleShape)
                        .testTag("detail_back_button")
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextDark)
                }

                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) SaleRed else TextMuted
                    )
                }
            }

            // Info Card
            Card(
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        color = SmartZoneBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = product.category,
                            color = SmartZoneBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = SmartZoneNavy
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Rs. ${String.format("%,.0f", product.price)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = SmartZoneNavy
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (product.originalPrice > product.price) {
                            Text(
                                text = "Rs. ${String.format("%,.0f", product.originalPrice)}",
                                fontSize = 14.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PayZy Card Banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE6FFFA)),
                        border = BorderStroke(1.dp, PayZyCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = PayZyCyan, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "PayZy",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pay in 4 interest-free installments of Rs ${String.format("%,.2f", payzyInstallment)} / month.",
                                fontSize = 11.sp,
                                color = TextDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Specs & Description
                    Text("Product Description", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = product.description,
                        fontSize = 13.sp,
                        color = TextDark,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reviews Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Customer Reviews & Ratings", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = RatingGold, modifier = Modifier.size(16.dp))
                                Text(" ${product.rating} (${reviews.size + product.reviewCount} reviews)", fontSize = 12.sp, color = TextMuted)
                            }
                        }

                        Button(
                            onClick = { showReviewDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SmartZoneNavy),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("write_review_btn")
                        ) {
                            Text("Write Review", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reviews List
                    if (reviews.isEmpty()) {
                        Text("No customer reviews yet. Be the first to write a review!", fontSize = 12.sp, color = TextMuted)
                    } else {
                        reviews.forEach { rev ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = LightBackground),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = rev.userName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = rev.date, fontSize = 10.sp, color = TextMuted)
                                    }
                                    Row {
                                        repeat(5) { index ->
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (index < rev.rating.toInt()) RatingGold else Color.LightGray,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = rev.comment, fontSize = 12.sp, color = TextDark)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        var reviewerName by remember { mutableStateOf("") }
        var reviewText by remember { mutableStateOf("") }
        var reviewRating by remember { mutableFloatStateOf(5.0f) }

        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Write a Product Review") },
            text = {
                Column {
                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select Rating:")
                    Row {
                        (1..5).forEach { star ->
                            IconButton(onClick = { reviewRating = star.toFloat() }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (star <= reviewRating) RatingGold else Color.LightGray
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Your Experience / Feedback") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewerName.isNotBlank() && reviewText.isNotBlank()) {
                            onSubmitReview(reviewerName, reviewRating, reviewText)
                            showReviewDialog = false
                        }
                    }
                ) {
                    Text("Submit Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
