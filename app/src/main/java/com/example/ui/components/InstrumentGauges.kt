package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.example.ui.theme.SteelTrayBevel
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Gauge(
    label: String,
    value: Float,
    unit: String,
    max: Float,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    val targetRatio = (value / max).coerceIn(0f, 1f)
    val animatedRatio by animateFloatAsState(
        targetValue = if (active) targetRatio else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "GaugeNeedle"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SteelTraySurface)
            .border(1.dp, SteelBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SteelDeepInk, SteelBase)
                    )
                )
                .border(2.dp, SteelTrayBevel, CircleShape)
        ) {
            Canvas(modifier = Modifier.size(90.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f - 4.dp.toPx()

                // Background dial arc (270 degrees)
                drawArc(
                    color = SteelBorder,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Active glowing arc
                drawArc(
                    color = CopperGlow,
                    startAngle = 135f,
                    sweepAngle = 270f * animatedRatio,
                    useCenter = false,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )

                // Tick marks
                for (i in 0..10) {
                    val angle = Math.toRadians((135 + i * 27).toDouble())
                    val innerR = radius - 10.dp.toPx()
                    val outerR = radius - 3.dp.toPx()

                    val start = Offset(
                        center.x + (innerR * cos(angle)).toFloat(),
                        center.y + (innerR * sin(angle)).toFloat()
                    )
                    val end = Offset(
                        center.x + (outerR * cos(angle)).toFloat(),
                        center.y + (outerR * sin(angle)).toFloat()
                    )

                    drawLine(
                        color = if (i <= (10 * animatedRatio)) CopperHighlight else SteelBorder,
                        start = start,
                        end = end,
                        strokeWidth = if (i % 5 == 0) 2.dp.toPx() else 1.dp.toPx()
                    )
                }

                // Needle
                val needleAngle = Math.toRadians((135 + 270 * animatedRatio).toDouble())
                val needleLength = radius - 8.dp.toPx()
                val needleEnd = Offset(
                    center.x + (needleLength * cos(needleAngle)).toFloat(),
                    center.y + (needleLength * sin(needleAngle)).toFloat()
                )

                drawLine(
                    color = CopperAccent,
                    start = center,
                    end = needleEnd,
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center hub
                drawCircle(
                    color = CopperHighlight,
                    radius = 5.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = SteelDeepInk,
                    radius = 2.dp.toPx(),
                    center = center
                )
            }

            // Numeric value overlay at bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
            ) {
                Text(
                    text = if (active) value.toInt().toString() else "--",
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = if (active) CopperHighlight else TextSecondary
                )
                Text(
                    text = unit,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
            }
        }
    }
}
