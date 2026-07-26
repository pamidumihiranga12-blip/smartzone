package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.CartItemWithProduct
import com.example.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItemWithProduct>,
    subtotal: Double,
    total: Double,
    discountPercentage: Double,
    onQuantityChange: (String, Int) -> Unit,
    onApplyCoupon: (String) -> Boolean,
    onProceedToCheckout: () -> Unit,
    onContinueShopping: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }
    var couponMessage by remember { mutableStateOf<String?>(null) }

    val shippingFee = if (subtotal > 10000.0 || subtotal == 0.0) 0.0 else 350.0

    Scaffold(
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(shadowElevation = 8.dp, color = Color.White) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Grand Total", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Rs. ${String.format("%,.2f", total)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = SmartZoneNavy
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onProceedToCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("proceed_to_checkout_btn")
                        ) {
                            Text("Proceed to Secure Checkout", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(LightBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Your Shopping Cart is Empty", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Explore our 4G/5G Routers, Antennas, and Unlock Services.", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onContinueShopping,
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue)
                    ) {
                        Text("Start Shopping")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(LightBackground)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Shopping Cart (${cartItems.size} Items)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = SmartZoneNavy
                    )
                }

                items(cartItems, key = { it.product.id }) { item ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = item.product.imageUrl,
                                contentDescription = item.product.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Rs. ${String.format("%,.0f", item.product.price)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SmartZoneNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(LightBackground)
                                    ) {
                                        IconButton(
                                            onClick = { onQuantityChange(item.product.id, item.quantity - 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                        Text(text = item.quantity.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        IconButton(
                                            onClick = { onQuantityChange(item.product.id, item.quantity + 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    IconButton(
                                        onClick = { onQuantityChange(item.product.id, 0) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = SaleRed, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Coupon Code Section
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Have a Promo Coupon Code?", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Use coupon 'SMART10' for 10% discount on order", fontSize = 11.sp, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("Enter SMART10") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val valid = onApplyCoupon(couponInput)
                                        couponMessage = if (valid) "Coupon Applied! 10% OFF" else "Invalid Coupon Code"
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SmartZoneNavy)
                                ) {
                                    Text("Apply")
                                }
                            }

                            couponMessage?.let { msg ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = msg,
                                    fontSize = 11.sp,
                                    color = if (discountPercentage > 0) PayZyCyan else SaleRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Breakdown summary
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Order Summary", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Subtotal", fontSize = 12.sp, color = TextMuted)
                                Text("Rs. ${String.format("%,.2f", subtotal)}", fontSize = 12.sp)
                            }
                            if (discountPercentage > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Coupon Discount (10%)", fontSize = 12.sp, color = PayZyCyan)
                                    Text("- Rs. ${String.format("%,.2f", subtotal * discountPercentage)}", fontSize = 12.sp, color = PayZyCyan)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Islandwide Courier Fee", fontSize = 12.sp, color = TextMuted)
                                Text(if (shippingFee == 0.0) "FREE" else "Rs. ${String.format("%,.2f", shippingFee)}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
