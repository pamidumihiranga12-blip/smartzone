package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FilterState
import com.example.model.OrderStatusStep
import com.example.model.SortOption
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    filterState: FilterState,
    onFilterStateChange: (FilterState) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("filter_sheet_content")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Advanced Search Filters",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SmartZoneNavy
                )
                TextButton(onClick = onReset) {
                    Text("Reset All", color = SaleRed)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Sort By Options
            Text("Sort By", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SortOption.values().forEach { option ->
                    FilterChip(
                        selected = filterState.sortBy == option,
                        onClick = { onFilterStateChange(filterState.copy(sortBy = option)) },
                        label = { Text(option.title, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price Range Slider
            Text(
                text = "Price Range: Rs. ${String.format("%,.0f", filterState.minPrice)} - Rs. ${String.format("%,.0f", filterState.maxPrice)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            RangeSlider(
                value = filterState.minPrice.toFloat()..filterState.maxPrice.toFloat(),
                onValueChange = { range ->
                    onFilterStateChange(
                        filterState.copy(
                            minPrice = range.start.toDouble(),
                            maxPrice = range.endInclusive.toDouble()
                        )
                    )
                },
                valueRange = 0f..50000f,
                steps = 20,
                colors = SliderDefaults.colors(
                    thumbColor = SmartZoneBlue,
                    activeTrackColor = SmartZoneBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sale Items Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show On Sale Items Only", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Switch(
                    checked = filterState.onlySale,
                    onCheckedChange = { onFilterStateChange(filterState.copy(onlySale = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = SmartZoneBlue)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Apply Filters", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderTrackingTimeline(
    currentStatus: String,
    trackingNumber: String,
    courierName: String,
    estimatedDate: String
) {
    val steps = listOf(
        OrderStatusStep("Order Placed", "Order received by SmartZone", true, currentStatus == "PLACED", "Jul 25, 10:30 AM"),
        OrderStatusStep("Processing & Verification", "SIM unlock/hardware verified", currentStatus in listOf("PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED"), currentStatus == "PROCESSING", "Jul 25, 02:00 PM"),
        OrderStatusStep("Shipped via Courier", "$courierName ($trackingNumber)", currentStatus in listOf("SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED"), currentStatus == "SHIPPED", "Jul 26, 08:00 AM"),
        OrderStatusStep("Out for Delivery", "Courier driver assigned", currentStatus in listOf("OUT_FOR_DELIVERY", "DELIVERED"), currentStatus == "OUT_FOR_DELIVERY", "Expected Today"),
        OrderStatusStep("Delivered", "Package handed over safely", currentStatus == "DELIVERED", currentStatus == "DELIVERED", estimatedDate)
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("tracking_timeline_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Live Courier Tracking", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                    Text(text = "$courierName • $trackingNumber", fontSize = 12.sp, color = TextMuted)
                }
                Surface(
                    color = PayZyCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = currentStatus,
                        color = PayZyCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            steps.forEachIndexed { index, step ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Timeline Node
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        step.isCurrent -> SmartZoneOrange
                                        step.isCompleted -> SmartZoneBlue
                                        else -> BorderColor
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (step.isCompleted && !step.isCurrent) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            } else if (step.isCurrent) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(36.dp)
                                    .background(if (step.isCompleted) SmartZoneBlue else BorderColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = step.title,
                            fontSize = 13.sp,
                            fontWeight = if (step.isCurrent || step.isCompleted) FontWeight.Bold else FontWeight.Normal,
                            color = if (step.isCurrent || step.isCompleted) TextDark else TextMuted
                        )
                        Text(text = step.subtitle, fontSize = 11.sp, color = TextMuted)
                        Text(text = step.timestamp, fontSize = 10.sp, color = SmartZoneBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentGatewaySelector(
    selectedMethod: String,
    onSelectMethod: (String) -> Unit,
    totalAmount: Double
) {
    val payzyFee = totalAmount * 0.14
    val payzyTotal = totalAmount * 1.14
    val payzyInstallment = payzyTotal / 4.0

    val methods = listOf(
        PaymentOption("PayZy 4-Installments", "4 x Rs ${String.format("%,.2f", payzyInstallment)} (14% Fee Included)", Icons.Default.CreditScore, PayZyCyan),
        PaymentOption("Credit / Debit Card", "Visa, Mastercard, AMEX Checkout", Icons.Default.CreditCard, SmartZoneBlue),
        PaymentOption("Cash On Delivery (COD)", "Pay with Cash upon delivery in Sri Lanka", Icons.Default.Payments, SmartZoneOrange),
        PaymentOption("Online Bank Transfer", "BOC Acc: 90231938 | IPMD WIJEGUNAWARDHANA", Icons.Default.AccountBalance, SmartZoneNavy)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Select Payment Method", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
        Spacer(modifier = Modifier.height(8.dp))

        methods.forEach { option ->
            val isSelected = selectedMethod == option.name
            Card(
                onClick = { onSelectMethod(option.name) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFF0F6FF) else Color.White),
                border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) SmartZoneBlue else BorderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("payment_option_${option.name.lowercase().replace(" ", "_")}")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectMethod(option.name) },
                            colors = RadioButtonDefaults.colors(selectedColor = SmartZoneBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(option.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = option.icon, contentDescription = null, tint = option.color)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = option.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(text = option.description, fontSize = 11.sp, color = TextMuted)
                        }
                    }

                    // Expanded Details for PayZy
                    if (isSelected && option.name == "PayZy 4-Installments") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = PayZyCyan.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("⚡ PayZy 4-Installment Plan Breakdown (14% Added)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Items Subtotal:", fontSize = 11.sp)
                                    Text("Rs. ${String.format("%,.2f", totalAmount)}", fontSize = 11.sp)
                                }
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("PayZy Processing Fee (14%):", fontSize = 11.sp, color = SmartZoneOrange)
                                    Text("+ Rs. ${String.format("%,.2f", payzyFee)}", fontSize = 11.sp, color = SmartZoneOrange, fontWeight = FontWeight.Bold)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("Total Payable Amount:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Rs. ${String.format("%,.2f", payzyTotal)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = PayZyCyan)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Monthly Payment Schedule (Split into 4):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("• 1st Payment (Today): Rs. ${String.format("%,.2f", payzyInstallment)}", fontSize = 10.sp)
                                Text("• 2nd Payment (Month 1): Rs. ${String.format("%,.2f", payzyInstallment)}", fontSize = 10.sp)
                                Text("• 3rd Payment (Month 2): Rs. ${String.format("%,.2f", payzyInstallment)}", fontSize = 10.sp)
                                Text("• 4th Payment (Month 3): Rs. ${String.format("%,.2f", payzyInstallment)}", fontSize = 10.sp)
                            }
                        }
                    }

                    // Expanded Details for Bank Transfer
                    if (isSelected && option.name == "Online Bank Transfer") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = SmartZoneNavy.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("🏦 Store Direct Bank Account Details", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Bank: BOC (Bank of Ceylon)", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                Text("Account Number: 90231938", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SmartZoneBlue)
                                Text("Account Name: IPMD WIJEGUNAWARDHANA", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text("Branch: Anuradhapura", fontSize = 11.sp, color = TextMuted)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Please send deposit receipt via WhatsApp to 078 68 000 86 for instant dispatch.", fontSize = 10.sp, color = SmartZoneOrange, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Expanded Details for Cash On Delivery
                    if (isSelected && option.name == "Cash On Delivery (COD)") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = SmartZoneOrange.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("🚚 Cash on Delivery (COD)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                                Text("Pay with exact cash in Sri Lankan Rupees (LKR) to the PromptX courier officer upon arrival in Anuradhapura or islandwide.", fontSize = 11.sp, color = TextDark)
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class PaymentOption(
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
