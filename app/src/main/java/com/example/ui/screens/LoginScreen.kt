package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.SmartZoneLogo
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    googleClientId: String,
    onLoginSuccess: (email: String, name: String) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: User Login, 1: Register, 2: Admin Login
    var email by remember { mutableStateOf("smartzonelk101@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var name by remember { mutableStateOf("Pamidu Wijegunawardhana") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SmartZoneNavy)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .testTag("login_card")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SmartZoneLogo(
                    phone = "078 68 000 86",
                    showDetails = true,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text("SmartZone Anuradhapura", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SmartZoneNavy)
                Text("4G/5G Routers & Unlocking Zone Sri Lanka", fontSize = 11.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(16.dp))

                // Login Mode Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = LightBackground,
                    contentColor = SmartZoneBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            email = "smartzonelk101@gmail.com"
                        },
                        text = { Text("Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Register", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            email = "admin@smartzone.lk"
                        },
                        text = { Text("Admin Panel", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(14.dp)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0 || selectedTab == 1) {
                    // Google Sign In Button
                    OutlinedButton(
                        onClick = {
                            onLoginSuccess("smartzonelk101@gmail.com", "SmartZone User")
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(SmartZoneBlue)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("google_login_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔑", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continue with Google Sign-In", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SmartZoneBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f))
                        Text(
                            if (selectedTab == 0) " OR EMAIL LOGIN " else " OR REGISTER WITH EMAIL ",
                            fontSize = 10.sp, color = TextMuted, modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (selectedTab == 1) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (selectedTab == 2) {
                    Surface(
                        color = SmartZoneNavy,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("🔐 Admin Access Portal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SmartZoneCyan)
                            Text("Sign in as store admin to edit site products, PayZy settings, and orders.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (selectedTab == 2) "Admin Email" else "Email Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val finalName = if (selectedTab == 2) "SmartZone Admin" else name
                        onLoginSuccess(email, finalName)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 2) SmartZoneOrange else SmartZoneBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("email_login_button")
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> "Sign In to Account"
                            1 -> "Create New Account"
                            else -> "Log In to Admin Dashboard"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onContinueAsGuest) {
                    Text("Continue as Guest →", color = TextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}
