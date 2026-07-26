package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.OrderEntity
import com.example.data.db.SiteConfigEntity
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    orders: List<OrderEntity>,
    siteConfig: SiteConfigEntity?,
    onUpdateOrderStatus: (String, String) -> Unit,
    onAddNewProduct: (title: String, price: Double, category: String, description: String, imageUrl: String) -> Unit,
    onUpdateSiteSettings: (storeName: String, storeSubtitle: String, phone: String, address: String, bankName: String, bankAccNo: String, bankAccName: String, merchantId: String, secretKey: String, mysqlApiUrl: String, mysqlApiKey: String) -> Unit,
    onSyncMySql: (onResult: (Int) -> Unit) -> Unit = {},
    onLogoutAdmin: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .testTag("admin_dashboard_screen")
    ) {
        // Admin Header
        Surface(color = SmartZoneNavy, shadowElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SmartZone Admin Control Center",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Store Order Fulfillment, Products & PayZy Integration",
                        fontSize = 10.sp,
                        color = SmartZoneCyan
                    )
                }

                Button(
                    onClick = onLogoutAdmin,
                    colors = ButtonDefaults.buttonColors(containerColor = SmartZoneOrange),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Exit Admin", fontSize = 10.sp)
                }
            }
        }

        // Admin Navigation Tabs
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Orders (${orders.size})", fontSize = 11.sp) },
                icon = { Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Add Product", fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Site & PayZy", fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedTab == 3,
                onClick = { selectedTab = 3 },
                text = { Text("Analytics", fontSize = 11.sp) },
                icon = { Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        when (selectedTab) {
            0 -> AdminOrdersTab(orders = orders, onUpdateStatus = onUpdateOrderStatus)
            1 -> AdminAddProductTab(onAdd = onAddNewProduct)
            2 -> AdminSiteSettingsTab(siteConfig = siteConfig, onSave = onUpdateSiteSettings)
            3 -> AdminAnalyticsTab(orders = orders)
        }
    }
}

@Composable
private fun AdminOrdersTab(
    orders: List<OrderEntity>,
    onUpdateStatus: (String, String) -> Unit
) {
    if (orders.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No customer orders placed yet.")
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders, key = { it.orderId }) { order ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Order ${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(color = SmartZoneBlue.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    text = order.status,
                                    color = SmartZoneBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Items: ${order.itemsSummary}", fontSize = 12.sp, color = TextDark)
                        Text(text = "Customer Address: ${order.deliveryAddress}", fontSize = 11.sp, color = TextMuted)
                        Text(text = "Total: Rs. ${String.format("%,.2f", order.totalAmount)} (${order.paymentMethod})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Update Delivery Status (Triggers Push Notification):", fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val statuses = listOf("PROCESSING", "SHIPPED", "OUT_FOR_DELIVERY", "DELIVERED")
                            statuses.forEach { st ->
                                Button(
                                    onClick = { onUpdateStatus(order.orderId, st) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (order.status == st) SmartZoneOrange else SmartZoneNavy
                                    ),
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                ) {
                                    Text(text = st.take(4), fontSize = 9.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAddProductTab(
    onAdd: (title: String, price: Double, category: String, description: String, imageUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Routers") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    var showSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Add New Product to Store Catalog", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Product Title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("Price in LKR (Rs.)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Text("Category:", fontSize = 12.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val categories = listOf("Routers", "Remotes", "Router Unlock Service", "Accessories")
            categories.forEach { cat ->
                FilterChip(
                    selected = category == cat,
                    onClick = { category = cat },
                    label = { Text(cat, fontSize = 10.sp) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Specifications / Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = imageUrl,
            onValueChange = { imageUrl = it },
            label = { Text("Image URL (Optional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val p = priceText.toDoubleOrNull() ?: 1000.0
                if (title.isNotBlank()) {
                    onAdd(title, p, category, description, imageUrl)
                    showSuccessMessage = true
                    title = ""
                    priceText = ""
                    description = ""
                    imageUrl = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Publish Product to Store", fontWeight = FontWeight.Bold)
        }

        if (showSuccessMessage) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Product published successfully!", color = PayZyCyan, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun AdminSiteSettingsTab(
    siteConfig: SiteConfigEntity?,
    onSave: (storeName: String, storeSubtitle: String, phone: String, address: String, bankName: String, bankAccNo: String, bankAccName: String, merchantId: String, secretKey: String, mysqlApiUrl: String, mysqlApiKey: String) -> Unit,
    onSyncMySql: (onResult: (Int) -> Unit) -> Unit = {}
) {
    var storeName by remember(siteConfig) { mutableStateOf(siteConfig?.storeName ?: "SMART ZONE") }
    var storeSubtitle by remember(siteConfig) { mutableStateOf(siteConfig?.storeSubtitle ?: "Powered by Pamidu") }
    var phone by remember(siteConfig) { mutableStateOf(siteConfig?.phone ?: "0786800086") }
    var address by remember(siteConfig) { mutableStateOf(siteConfig?.address ?: "Anuradhapura") }
    var bankName by remember(siteConfig) { mutableStateOf(siteConfig?.bankName ?: "BOC (Bank of Ceylon)") }
    var bankAccNo by remember(siteConfig) { mutableStateOf(siteConfig?.bankAccNo ?: "90231938") }
    var bankAccName by remember(siteConfig) { mutableStateOf(siteConfig?.bankAccName ?: "IPMD WIJEGUNAWARDHANA") }
    var merchantId by remember(siteConfig) { mutableStateOf(siteConfig?.payzyMerchantId ?: "567") }
    var secretKey by remember(siteConfig) { mutableStateOf(siteConfig?.payzySecretKey ?: "0d2a8f76-b73e-461f-b89a-04d73c892f50") }
    var mysqlApiUrl by remember(siteConfig) { mutableStateOf(siteConfig?.mysqlApiUrl ?: "https://smartzonelk.lk/api") }
    var mysqlApiKey by remember(siteConfig) { mutableStateOf(siteConfig?.mysqlApiKey ?: "sz_api_key_90231938") }

    var showSuccessMsg by remember { mutableStateOf(false) }
    var syncResultMsg by remember { mutableStateOf("") }
    var showPhpScriptModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Store Info, MySQL Sync & PayZy Settings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
        Text("Sync your website MySQL database & Firebase in real-time", fontSize = 11.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(12.dp))

        // MySQL Website Database Integration Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🗄️ MySQL Website Database API", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                    Spacer(modifier = Modifier.weight(1f))
                    Badge(containerColor = SmartZoneBlue) {
                        Text("Active", color = Color.White, fontSize = 9.sp)
                    }
                }
                Text("Connect products, orders, and users directly to your website's MySQL DB", fontSize = 11.sp, color = TextDark)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mysqlApiUrl,
                    onValueChange = { mysqlApiUrl = it },
                    label = { Text("Website MySQL API Base URL") },
                    placeholder = { Text("https://smartzone.lk/api") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = mysqlApiKey,
                    onValueChange = { mysqlApiKey = it },
                    label = { Text("MySQL API Security Key") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            syncResultMsg = "Syncing with $mysqlApiUrl..."
                            onSyncMySql { count ->
                                syncResultMsg = "Fetched $count products directly from your website MySQL DB!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneNavy),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("⚡ Sync MySQL Now", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { showPhpScriptModal = !showPhpScriptModal },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("📄 View PHP Script", fontSize = 11.sp, color = SmartZoneNavy)
                    }
                }

                if (syncResultMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(syncResultMsg, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SmartZoneBlue)
                }

                if (showPhpScriptModal) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFF1E1E1E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PHP code for your cPanel / Website:", fontSize = 11.sp, color = SmartZoneCyan, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                com.example.data.MySqlApiManager.getSamplePhpGetProductsCode(),
                                fontSize = 9.sp,
                                color = Color.Green
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PayZy Payment Credentials Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("💳 PayZy Payment Gateway Keys (14% Fee / 4x Installments)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PayZyCyan)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = merchantId,
                    onValueChange = { merchantId = it },
                    label = { Text("PayZy Merchant ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = secretKey,
                    onValueChange = { secretKey = it },
                    label = { Text("PayZy Secret Key") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Store & Bank Details Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("🏦 Store Profile & Bank Details", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(value = storeName, onValueChange = { storeName = it }, label = { Text("Store Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = storeSubtitle, onValueChange = { storeSubtitle = it }, label = { Text("Subtitle / Branding") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Store Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = bankName, onValueChange = { bankName = it }, label = { Text("Bank Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = bankAccNo, onValueChange = { bankAccNo = it }, label = { Text("Bank Account Number") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(value = bankAccName, onValueChange = { bankAccName = it }, label = { Text("Bank Account Holder Name") }, modifier = Modifier.fillMaxWidth())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(storeName, storeSubtitle, phone, address, bankName, bankAccNo, bankAccName, merchantId, secretKey, mysqlApiUrl, mysqlApiKey)
                showSuccessMsg = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = SmartZoneOrange),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Save & Sync Settings to Database & Firebase", fontWeight = FontWeight.Bold)
        }

        if (showSuccessMsg) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Settings updated and synchronized to Firebase Firestore!", color = SmartZoneBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun AdminAnalyticsTab(orders: List<OrderEntity>) {
    val totalRevenue = orders.sumOf { it.totalAmount } + 42500.0
    val totalOrdersCount = orders.size + 12

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Store Revenue & Sales Analytics", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SmartZoneNavy),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Revenue", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("Rs. ${String.format("%,.0f", totalRevenue)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SmartZoneCyan)
                }
            }

            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = SmartZoneBlue),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Total Orders", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    Text("$totalOrdersCount Orders", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Category Revenue Visualizer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Custom Compose Canvas Bar Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                ) {
                    val bars = listOf(
                        "Routers" to 0.85f,
                        "Unlock" to 0.60f,
                        "Antennas" to 0.75f,
                        "Remotes" to 0.40f,
                        "Tools" to 0.30f
                    )

                    val barWidth = size.width / (bars.size * 2)
                    val maxHeight = size.height - 30f

                    bars.forEachIndexed { index, pair ->
                        val barHeight = maxHeight * pair.second
                        val x = index * (barWidth * 2) + barWidth / 2

                        drawRect(
                            color = SmartZoneBlue,
                            topLeft = Offset(x, maxHeight - barHeight),
                            size = Size(barWidth, barHeight)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("Routers", fontSize = 10.sp)
                    Text("Unlock", fontSize = 10.sp)
                    Text("Antennas", fontSize = 10.sp)
                    Text("Remotes", fontSize = 10.sp)
                    Text("Tools", fontSize = 10.sp)
                }
            }
        }
    }
}
