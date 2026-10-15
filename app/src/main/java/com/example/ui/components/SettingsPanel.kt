package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ServerNode
import com.example.model.VpnSettings
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperBevel
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ConfigParser

@Composable
fun SettingsPanel(
    settings: VpnSettings,
    onSettingsChange: (VpnSettings) -> Unit,
    activeNode: ServerNode?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showJsonExport by remember { mutableStateOf(false) }

    val dnsOptions = listOf(
        "1.1.1.1 (Cloudflare)",
        "8.8.8.8 (Google)",
        "94.140.14.14 (AdGuard)",
        "9.9.9.9 (Quad9)"
    )

    val routingOptions = listOf(
        "Global",
        "Bypass LAN",
        "Bypass LAN & Domestic",
        "Rule-based"
    )

    val protocolsList = listOf("vless", "vmess", "trojan", "shadowsocks")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)
    ) {
        // Security & Kill Switch Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Security, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("SECURITY & NETWORK LOCK", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Kill Switch", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Block all non-VPN internet traffic if connection drops unexpectedly.", fontSize = 11.sp, color = TextSecondary)
                }
                Switch(
                    checked = settings.killSwitch,
                    onCheckedChange = { onSettingsChange(settings.copy(killSwitch = it)) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = SteelDeepInk,
                        checkedTrackColor = CopperAccent,
                        uncheckedThumbColor = TextSecondary,
                        uncheckedTrackColor = SteelBase
                    ),
                    modifier = Modifier.testTag("kill_switch_toggle")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // DNS Configuration
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Dns, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("DNS RESOLVER SETTINGS", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(10.dp))

            dnsOptions.forEach { dns ->
                val isSelected = settings.dnsServer == dns
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SteelDeepInk else SteelBase)
                        .border(1.dp, if (isSelected) CopperAccent else SteelBorder, RoundedCornerShape(8.dp))
                        .clickable { onSettingsChange(settings.copy(dnsServer = dns)) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = dns,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) CopperGlow else TextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Protocol Selector & Routing Rules
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Route, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("PROTOCOL FILTER & ROUTING", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("ENABLED PROTOCOLS", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                protocolsList.forEach { proto ->
                    val checked = settings.selectedProtocols.contains(proto)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SteelDeepInk)
                            .border(1.dp, if (checked) CopperAccent else SteelBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                val current = settings.selectedProtocols.toMutableList()
                                if (checked && current.size > 1) current.remove(proto) else if (!checked) current.add(proto)
                                onSettingsChange(settings.copy(selectedProtocols = current))
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(checkedColor = CopperAccent, checkmarkColor = SteelDeepInk),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(proto.uppercase(), fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("TRAFFIC ROUTING POLICY", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))

            routingOptions.forEach { rule ->
                val isSelected = settings.routingRule == rule
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SteelDeepInk else SteelBase)
                        .border(1.dp, if (isSelected) CopperAccent else SteelBorder, RoundedCornerShape(8.dp))
                        .clickable { onSettingsChange(settings.copy(routingRule = rule)) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = rule,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = if (isSelected) CopperGlow else TextPrimary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Local Proxy Ports & Config JSON Export
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SteelTraySurface)
                .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Code, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("XRAY CONFIG JSON EXPORT", fontSize = 12.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperHighlight)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SOCKS5 Proxy Port: 10808", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                Text("HTTP Proxy Port: 10809", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(10.dp))

            val xrayJson = ConfigParser.generateXrayConfigJson(activeNode, settings.dnsServer, settings.routingRule)

            Button(
                onClick = { showJsonExport = !showJsonExport },
                colors = ButtonDefaults.buttonColors(containerColor = CopperBevel, contentColor = CopperHighlight),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().testTag("export_json_button")
            ) {
                Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (showJsonExport) "HIDE XRAY CONFIG JSON" else "GENERATE XRAY JSON CONFIG", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            }

            if (showJsonExport) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SteelDeepInk)
                        .padding(10.dp)
                ) {
                    OutlinedTextField(
                        value = xrayJson,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().height(180.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = CopperGlow, unfocusedTextColor = CopperGlow)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Xray Config JSON", xrayJson)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied Xray JSON config to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CopperAccent, contentColor = SteelDeepInk),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("COPY TO CLIPBOARD", fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
