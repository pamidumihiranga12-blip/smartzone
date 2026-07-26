package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.OrderEntity
import com.example.ui.components.OrderTrackingTimeline
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    orders: List<OrderEntity>,
    onSimulateStatusUpdate: (String, String) -> Unit
) {
    var selectedOrder by remember { mutableStateOf<OrderEntity?>(orders.firstOrNull()) }

    LaunchedEffect(orders) {
        if (selectedOrder == null && orders.isNotEmpty()) {
            selectedOrder = orders.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(16.dp)
            .testTag("order_tracking_screen")
    ) {
        Text(
            text = "Delivery & Order Tracking",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = SmartZoneNavy
        )
        Text(
            text = "Track your 4G/5G Routers & Unlock Services in real-time.",
            fontSize = 12.sp,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No active orders found.", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Place an order to see live courier tracking.", fontSize = 12.sp, color = TextMuted)
                }
            }
        } else {
            // Select Order Dropdown/Chips
            Text("Select Active Order:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                orders.take(4).forEach { ord ->
                    FilterChip(
                        selected = selectedOrder?.orderId == ord.orderId,
                        onClick = { selectedOrder = ord },
                        label = { Text(ord.orderId, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            selectedOrder?.let { activeOrd ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    // Order Info Card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Order ${activeOrd.orderId}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                                    Text("Rs. ${String.format("%,.0f", activeOrd.totalAmount)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SmartZoneBlue)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = activeOrd.itemsSummary, fontSize = 12.sp, color = TextDark)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Payment: ${activeOrd.paymentMethod}", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }

                    // Timeline
                    item {
                        OrderTrackingTimeline(
                            currentStatus = activeOrd.status,
                            trackingNumber = activeOrd.trackingNumber,
                            courierName = activeOrd.courierName,
                            estimatedDate = activeOrd.estimatedDeliveryDate
                        )
                    }

                    // Demo Simulator Button
                    item {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                            border = BorderStroke(1.dp, SmartZoneBlue)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🧪 Live Tracking Simulator", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneBlue)
                                Text("Advance tracking status to test real-time push notifications:", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    val nextStatus = when (activeOrd.status) {
                                        "PLACED" -> "PROCESSING"
                                        "PROCESSING" -> "SHIPPED"
                                        "SHIPPED" -> "OUT_FOR_DELIVERY"
                                        "OUT_FOR_DELIVERY" -> "DELIVERED"
                                        else -> "DELIVERED"
                                    }
                                    Button(
                                        onClick = {
                                            onSimulateStatusUpdate(activeOrd.orderId, nextStatus)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneNavy),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Advance to $nextStatus", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
