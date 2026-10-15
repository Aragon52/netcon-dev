package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.util.LanguageManager

@Composable
fun LanguageSelector(
    open: Boolean,
    currentLang: LanguageManager.Language,
    onSelect: (LanguageManager.Language) -> Unit,
    onDismiss: () -> Unit
) {
    if (!open) return

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SteelBase,
        title = {
            Text(
                text = "SELECT LANGUAGE",
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = CopperHighlight
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                LanguageManager.Language.values().forEach { lang ->
                    val isSelected = lang == currentLang
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) SteelDeepInk else SteelBase)
                            .border(1.dp, if (isSelected) CopperAccent else SteelBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                onSelect(lang)
                                onDismiss()
                            }
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lang.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = lang.label,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) CopperGlow else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = CopperAccent
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
