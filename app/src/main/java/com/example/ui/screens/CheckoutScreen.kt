package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserProfileEntity
import com.example.ui.components.PaymentGatewaySelector
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    totalAmount: Double,
    userProfile: UserProfileEntity?,
    payzyFeePercentage: Double = 14.0,
    onPlaceOrder: (paymentMethod: String, address: String, onSuccess: (String) -> Unit) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToTracking: (String) -> Unit
) {
    var deliveryName by remember { mutableStateOf(userProfile?.name ?: "SmartZone Customer") }
    var deliveryPhone by remember { mutableStateOf(userProfile?.phone?.ifBlank { "0786800086" } ?: "0786800086") }
    var deliveryAddress by remember { mutableStateOf(userProfile?.address?.ifBlank { "Anuradhapura" } ?: "Anuradhapura") }

    var selectedPaymentMethod by remember { mutableStateOf("PayZy 4-Installments") }

    val effectiveTotal = if (selectedPaymentMethod.contains("PayZy")) totalAmount * (1.0 + (payzyFeePercentage / 100.0)) else totalAmount

    // Card Details state
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8821") }
    var cardExpiry by remember { mutableStateOf("12/28") }
    var cardCvc by remember { mutableStateOf("382") }

    var confirmedOrderId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout Gateway", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SmartZoneNavy, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total to Pay", fontSize = 14.sp, color = TextMuted)
                        Text("Rs. ${String.format("%,.2f", effectiveTotal)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SmartZoneNavy)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            val fullAddr = "$deliveryName ($deliveryPhone), $deliveryAddress"
                            onPlaceOrder(selectedPaymentMethod, fullAddr) { newOrderId ->
                                confirmedOrderId = newOrderId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("place_order_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Pay & Confirm Order", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(LightBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Shipping Address Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Delivery Shipping Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = deliveryName,
                        onValueChange = { deliveryName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliveryPhone,
                        onValueChange = { deliveryPhone = it },
                        label = { Text("Phone Number (For Courier Updates)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Street Address & City (Sri Lanka)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PaymentGatewaySelector(
                        selectedMethod = selectedPaymentMethod,
                        onSelectMethod = { selectedPaymentMethod = it },
                        totalAmount = totalAmount
                    )

                    // If Card chosen, show mock fields
                    if (selectedPaymentMethod == "Credit / Debit Card") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Card Number (Visa / Mastercard)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { cardExpiry = it },
                                label = { Text("MM/YY") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvc,
                                onValueChange = { cardCvc = it },
                                label = { Text("CVC") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }

    // Success Order Confirmation Modal
    confirmedOrderId?.let { orderId ->
        AlertDialog(
            onDismissRequest = {},
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PayZyCyan, modifier = Modifier.size(48.dp))
            },
            title = {
                Text("Order Placed Successfully!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    Text("Your order $orderId has been placed.")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("We sent a real-time push notification update to your profile.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Courier: PromptX Express Sri Lanka", fontWeight = FontWeight.Bold)
                    Text("Tracking ID: TRK-${orderId.replace("#", "")}-SLP")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idToTrack = orderId
                        confirmedOrderId = null
                        onNavigateToTracking(idToTrack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue)
                ) {
                    Text("Track Order Now")
                }
            }
        )
    }
}
