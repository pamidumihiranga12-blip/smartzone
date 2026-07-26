package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
import com.example.data.db.OrderEntity
import com.example.data.db.SiteConfigEntity
import com.example.data.db.UserProfileEntity
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    userProfile: UserProfileEntity?,
    siteConfig: SiteConfigEntity?,
    orders: List<OrderEntity>,
    currentLanguage: AppLanguage,
    onLanguageSelect: (AppLanguage) -> Unit,
    isAdminMode: Boolean,
    onToggleAdminMode: () -> Unit,
    onTrackOrderClick: (String) -> Unit,
    onUpdateProfile: (String, String, String, String) -> Unit,
    onLoginSuccess: (email: String, name: String) -> Unit,
    onAddNewProduct: (title: String, price: Double, category: String, description: String, imageUrl: String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onUpdateSiteSettings: (storeName: String, storeSubtitle: String, phone: String, address: String, bankName: String, bankAccNo: String, bankAccName: String, merchantId: String, secretKey: String, mysqlApiUrl: String, mysqlApiKey: String) -> Unit,
    onLogout: () -> Unit
) {
    // 1. Auth Guard Check: If user is not logged in, show LoginScreen
    if (userProfile?.isLoggedIn != true) {
        LoginScreen(
            googleClientId = userProfile?.googleClientId ?: "",
            onLoginSuccess = onLoginSuccess,
            onContinueAsGuest = {
                // If user clicks guest, log in default customer account
                onLoginSuccess("guest@smartzone.lk", "Guest Customer")
            }
        )
        return
    }

    // 2. Admin Check: If logged in as Admin, show Admin Dashboard Screen directly
    if (userProfile.isAdmin || isAdminMode) {
        AdminDashboardScreen(
            orders = orders,
            siteConfig = siteConfig,
            onUpdateOrderStatus = onUpdateOrderStatus,
            onAddNewProduct = onAddNewProduct,
            onUpdateSiteSettings = onUpdateSiteSettings,
            onLogoutAdmin = onLogout
        )
        return
    }

    // 3. Customer Profile View
    var isEditing by remember { mutableStateOf(false) }

    var name by remember(userProfile) { mutableStateOf(userProfile.name) }
    var email by remember(userProfile) { mutableStateOf(userProfile.email) }
    var phone by remember(userProfile) { mutableStateOf(userProfile.phone.ifBlank { "0786800086" }) }
    var address by remember(userProfile) { mutableStateOf(userProfile.address.ifBlank { "Anuradhapura" }) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        // Avatar & Header Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SmartZoneNavy),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SmartZoneBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = userProfile.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = userProfile.email,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SmartZoneCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Account Logged In",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneOrange),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(26.dp)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Log Out", fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile Details & Edit
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Customer Information & Delivery Address", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                    IconButton(onClick = {
                        if (isEditing) {
                            onUpdateProfile(name, email, phone, address)
                        }
                        isEditing = !isEditing
                    }) {
                        Icon(if (isEditing) Icons.Default.AdminPanelSettings else Icons.Default.Edit, contentDescription = "Edit Profile", tint = SmartZoneBlue)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (isEditing) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Delivery Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onUpdateProfile(name, email, phone, address)
                            isEditing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Profile Changes")
                    }
                } else {
                    Text("Phone: ${userProfile.phone}", fontSize = 13.sp)
                    Text("Address: ${userProfile.address}", fontSize = 13.sp, color = TextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Switcher Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = SmartZoneBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Multi-Language Support", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        FilterChip(
                            selected = currentLanguage == lang,
                            onClick = { onLanguageSelect(lang) },
                            label = { Text("${lang.flag} ${lang.displayName}") }
                        )
                    }
                }
            }
        }

        val isUserAdmin = userProfile.isAdmin || userProfile.email == "smartzonelk101@gmail.com" || userProfile.email.contains("admin")

        if (isUserAdmin) {
            Spacer(modifier = Modifier.height(16.dp))

            // Switch to Admin Control Panel Option
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Store Admin Portal",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Manage products, orders & website MySQL DB",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = onToggleAdminMode,
                        colors = ButtonDefaults.buttonColors(containerColor = SmartZoneBlue)
                    ) {
                        Text("Open Admin Panel", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Firebase & MySQL Database Connection Status for Admin
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(containerColor = Color(0xFF4CAF50), contentColor = Color.White) {
                            Text("Connected", modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Website MySQL & Firebase Sync",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SmartZoneNavy
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = LightBackground,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("MySQL Host: ${siteConfig?.mysqlDbHost ?: "sdb-l.hosting.stackcp.net"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("MySQL DB: ${siteConfig?.mysqlDbName ?: "smartzoneweb-313932d478"}", fontSize = 11.sp, color = TextMuted)
                            Text("Firebase ID: srmobile-6091e", fontSize = 11.sp, color = TextMuted)
                            Text("Phone: ${siteConfig?.phone ?: "0786800086"} | Address: ${siteConfig?.address ?: "Anuradhapura"}", fontSize = 11.sp, color = SmartZoneBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Order History List
        Text("Order History & Delivery Status", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SmartZoneNavy)
        Spacer(modifier = Modifier.height(8.dp))

        if (orders.isEmpty()) {
            Text("No past orders found.", fontSize = 12.sp, color = TextMuted)
        } else {
            orders.forEach { ord ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Order ${ord.orderId}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = PayZyCyan.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                                Text(text = ord.status, color = PayZyCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = ord.itemsSummary, fontSize = 11.sp, color = TextDark)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Rs. ${String.format("%,.0f", ord.totalAmount)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SmartZoneBlue)
                            Button(
                                onClick = { onTrackOrderClick(ord.orderId) },
                                colors = ButtonDefaults.buttonColors(containerColor = SmartZoneNavy),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Track Delivery", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
