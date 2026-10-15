package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk

@Composable
fun OdometerView(
    valueMb: Float,
    modifier: Modifier = Modifier
) {
    val formatted = String.format("%.1f", valueMb)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(SteelDeepInk)
            .border(1.dp, SteelBorder, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = formatted,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = CopperHighlight,
            letterSpacing = 1.sp
        )
    }
}
