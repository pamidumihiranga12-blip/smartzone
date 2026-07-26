package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SmartZoneCyan
import com.example.ui.theme.SmartZoneNavy
import com.example.ui.theme.SmartZoneOrange
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SmartZoneNavy),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            try {
                Image(
                    painter = painterResource(id = R.drawable.smartzone_logo),
                    contentDescription = "SmartZone Opening Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(240.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(20.dp))
                )
            } catch (e: Throwable) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .scale(scale.value)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SmartZoneCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SZ",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SMART ZONE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Text(
                text = "POWERED BY PAMIDU",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SmartZoneCyan,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Unlocking Zone & Tech Store Anuradhapura",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )

            Text(
                text = "📞 078 68 000 86",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SmartZoneOrange
            )

            Spacer(modifier = Modifier.height(40.dp))

            CircularProgressIndicator(
                color = SmartZoneCyan,
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp
            )
        }
    }
}
