package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Power
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.VpnStatus
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.EngraveInk
import com.example.ui.theme.LedGreen
import com.example.ui.theme.LedRed
import com.example.ui.theme.LedYellow
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelPlateLight
import com.example.ui.theme.SteelPlateMid
import com.example.ui.theme.SteelPlateShade
import com.example.ui.theme.SteelTrayBevel
import com.example.ui.theme.TextSecondary

@Composable
fun PowerControl(
    status: VpnStatus,
    duration: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "PowerGlow")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val auraColor by animateColorAsState(
        targetValue = when (status) {
            VpnStatus.CONNECTED -> LedGreen.copy(alpha = 0.35f)
            VpnStatus.CONNECTING -> LedYellow.copy(alpha = 0.35f)
            VpnStatus.OFFLINE -> CopperAccent.copy(alpha = 0.15f)
        },
        label = "AuraColor"
    )

    val buttonColor by animateColorAsState(
        targetValue = when (status) {
            VpnStatus.CONNECTED -> LedGreen
            VpnStatus.CONNECTING -> LedYellow
            VpnStatus.OFFLINE -> CopperAccent
        },
        label = "ButtonColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(130.dp)
        ) {
            // Pulse outer aura
            Box(
                modifier = Modifier
                    .size(125.dp)
                    .scale(if (status == VpnStatus.CONNECTING || status == VpnStatus.CONNECTED) pulseScale else 1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(auraColor)
            )

            // Raised steel switch housing
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(width = 112.dp, height = 116.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SteelTrayBevel, SteelBase, SteelDeepInk)
                        )
                    )
                    .border(3.dp, SteelTrayBevel, RoundedCornerShape(7.dp))
            ) {
                val switchPlate = if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) {
                    listOf(SteelPlateShade, SteelPlateMid, SteelPlateLight)
                } else {
                    listOf(SteelPlateLight, SteelPlateMid, SteelPlateShade)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(width = 78.dp, height = 86.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.verticalGradient(colors = switchPlate)
                        )
                        .border(2.dp, SteelDeepInk, RoundedCornerShape(4.dp))
                        .testTag("power_button")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick
                        )
                ) {
                    Text(
                        text = "I",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EngraveInk,
                        modifier = Modifier.align(Alignment.TopCenter).padding(top = 5.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Power,
                        contentDescription = "Toggle VPN",
                        tint = buttonColor,
                        modifier = Modifier.size(34.dp)
                    )
                    Text(
                        text = "O",
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = EngraveInk,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Power state label
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SteelDeepInk)
                .border(1.dp, SteelBorder, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = when (status) {
                    VpnStatus.OFFLINE -> "TAP TO CONNECT"
                    VpnStatus.CONNECTING -> "SECURE HANDSHAKE"
                    VpnStatus.CONNECTED -> "TAP TO DISCONNECT"
                },
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = when (status) {
                    VpnStatus.OFFLINE -> TextSecondary
                    VpnStatus.CONNECTING -> LedYellow
                    VpnStatus.CONNECTED -> CopperHighlight
                },
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = if (status == VpnStatus.CONNECTED) duration else "--:--:--",
            fontSize = 13.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = if (status == VpnStatus.CONNECTED) CopperGlow else TextSecondary
        )
    }
}
