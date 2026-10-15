package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private data class TourStep(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun WelcomeDialog(
    open: Boolean,
    onDismiss: () -> Unit
) {
    if (!open) return

    val steps = listOf(
        TourStep(
            title = "PRECISION CONNECTION SYSTEM",
            subtitle = "Welcome to Netcon VPN",
            description = "Engineered with a brushed-steel and burnished-copper instrument design for high-performance V2Ray and native Android VPN tunneling.",
            icon = Icons.Default.CompassCalibration
        ),
        TourStep(
            title = "SMART AI RECOMMENDATION",
            subtitle = "Ask Gemini AI",
            description = "Describe what you want to use the VPN for (e.g., PUBG Gaming, 4K Video Streaming, Anti-Censorship). Gemini will recommend the best server and protocol.",
            icon = Icons.Default.AutoAwesome
        ),
        TourStep(
            title = "ZERO LOGS GUARANTEE",
            subtitle = "Built for Total Privacy",
            description = "All keys and server configuration links are parsed directly on your Android device. Zero logs recorded.",
            icon = Icons.Default.Security
        )
    )

    var currentStepIdx by remember { mutableIntStateOf(0) }
    val step = steps[currentStepIdx]

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SteelBase,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = step.icon,
                    contentDescription = null,
                    tint = CopperAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = step.title,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CopperHighlight,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = step.subtitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Text(
                    text = step.description,
                    fontSize = 13.sp,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    steps.indices.forEach { index ->
                        val isCurrent = index == currentStepIdx
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(width = if (isCurrent) 24.dp else 8.dp, height = 8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isCurrent) CopperAccent else SteelBorder)
                        ) {}
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentStepIdx < steps.size - 1) {
                        currentStepIdx++
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = CopperAccent,
                    contentColor = SteelDeepInk
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (currentStepIdx < steps.size - 1) "NEXT" else "GET STARTED",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            if (currentStepIdx > 0) {
                Button(
                    onClick = { currentStepIdx-- },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SteelDeepInk,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("BACK", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    )
}
