package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiRecommendation
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperBevel
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.LedRed
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelTrayBevel
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTrayPanel(
    purpose: String,
    onPurposeChange: (String) -> Unit,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    aiBusy: Boolean,
    onAskAi: () -> Unit,
    recommendation: AiRecommendation?,
    error: String?,
    onApplyRecommendation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showApiKey by remember { mutableStateOf(false) }

    val quickActions = listOf(
        "PUBG Mobile",
        "Facebook & IG",
        "YouTube 4K",
        "Low Latency Gaming",
        "Anti-Censorship"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SteelTraySurface)
            .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        // AI Tray Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CopperBevel)
                    .border(1.dp, CopperAccent, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CopperHighlight,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "AI · Ask Gemini",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CopperHighlight
                )
            }

            Text(
                text = "A SMARTER WAY TO CONNECT",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "WHAT DO YOU WANT TO USE THE VPN FOR?",
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Purpose Input Field
        OutlinedTextField(
            value = purpose,
            onValueChange = onPurposeChange,
            placeholder = {
                Text(
                    text = "Type platform, purpose, or paste config link...",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = CopperAccent,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("purpose_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SteelDeepInk,
                unfocusedContainerColor = SteelDeepInk,
                focusedBorderColor = CopperAccent,
                unfocusedBorderColor = SteelBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Action Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            quickActions.forEach { action ->
                val isSelected = purpose == action
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) CopperAccent else SteelBase)
                        .border(1.dp, if (isSelected) CopperHighlight else SteelBorder, RoundedCornerShape(6.dp))
                        .clickable { onPurposeChange(action) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = action,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) SteelDeepInk else TextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // API Key Field & Find My Config Action
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                placeholder = { Text("Gemini API key (optional)", fontSize = 11.sp, color = TextSecondary) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { showApiKey = !showApiKey }) {
                        Icon(
                            imageVector = if (showApiKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle API Key",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .weight(1f)
                    .testTag("gemini_api_key_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SteelDeepInk,
                    unfocusedContainerColor = SteelDeepInk,
                    focusedBorderColor = CopperAccent,
                    unfocusedBorderColor = SteelBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Button(
                onClick = onAskAi,
                enabled = !aiBusy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CopperAccent,
                    contentColor = SteelDeepInk
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("find_config_button")
            ) {
                if (aiBusy) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = SteelDeepInk,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (aiBusy) "ANALYZING…" else "FIND CONFIG",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Keys stay in this session. Config links are parsed on device.",
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
        }

        // Error message
        AnimatedVisibility(visible = error != null) {
            Text(
                text = error ?: "",
                fontSize = 11.sp,
                color = LedRed,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Recommendation result banner
        AnimatedVisibility(visible = recommendation != null) {
            recommendation?.let { rec ->
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SteelDeepInk)
                        .border(1.dp, CopperAccent, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CopperGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI RECOMMENDATION",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CopperHighlight
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = rec.reason,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onApplyRecommendation(rec.serverId) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CopperAccent,
                            contentColor = SteelDeepInk
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("apply_recommendation_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "USE RECOMMENDED SERVER",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
