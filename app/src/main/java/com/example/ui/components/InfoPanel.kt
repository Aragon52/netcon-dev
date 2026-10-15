package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerNode
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperBevel
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun InfoPanel(
    node: ServerNode?,
    connected: Boolean,
    onOpenWelcome: () -> Unit,
    onOpenLanguage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)
    ) {
        // App Diagnostics Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("NETCON SYSTEM DIAGNOSTICS", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(12.dp))

            DiagnosticItem("App Version", "2.4.1 (Native Android)")
            DiagnosticItem("Client Core", "Xray-core / V2Ray Native Engine")
            DiagnosticItem("TUN Interface IP", if (connected) "10.8.0.2 / 24" else "Disconnected")
            DiagnosticItem("Active Server", node?.name ?: "None")
            DiagnosticItem("Selected Protocol", node?.protocol?.uppercase() ?: "VLESS")
            DiagnosticItem("Zero-Log Status", "ENFORCED & ACTIVE")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Zero-Log Privacy Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Shield, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ZERO-LOG & DATA SAFETY", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Netcon operates under strict zero-knowledge principles. No activity logs, browsing history, or connection metadata are ever recorded or transmitted.",
                fontSize = 12.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "All configuration links, VMess/VLess keys, and API credentials are processed strictly on your local device.",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Actions (Language & Onboarding)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onOpenLanguage,
                colors = ButtonDefaults.buttonColors(containerColor = SteelTraySurface, contentColor = TextPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).border(1.dp, SteelBorder, RoundedCornerShape(10.dp)).testTag("language_button")
            ) {
                Icon(Icons.Default.Language, contentDescription = null, tint = CopperHighlight, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LANGUAGE", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onOpenWelcome,
                colors = ButtonDefaults.buttonColors(containerColor = CopperBevel, contentColor = CopperHighlight),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f).testTag("onboarding_button")
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("GUIDE / TOUR", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DiagnosticItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
