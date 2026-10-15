package com.example

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppTab
import com.example.model.VpnStatus
import com.example.ui.components.ActiveServerCard
import com.example.ui.components.AiTrayPanel
import com.example.ui.components.Gauge
import com.example.ui.components.InfoPanel
import com.example.ui.components.LanguageSelector
import com.example.ui.components.OdometerView
import com.example.ui.components.PowerControl
import com.example.ui.components.ServerManagerDrawer
import com.example.ui.components.SettingsPanel
import com.example.ui.components.WelcomeDialog
import com.example.ui.theme.CopperAccent
import com.example.ui.theme.CopperGlow
import com.example.ui.theme.CopperHighlight
import com.example.ui.theme.LedGreen
import com.example.ui.theme.LedRed
import com.example.ui.theme.LedYellow
import com.example.ui.theme.NetconTheme
import com.example.ui.theme.SteelBase
import com.example.ui.theme.SteelBorder
import com.example.ui.theme.SteelDark
import com.example.ui.theme.SteelDeepInk
import com.example.ui.theme.SteelTrayBevel
import com.example.ui.theme.SteelTraySurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VpnViewModel by viewModels()

    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.startVpnService(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NetconTheme {
                Scaffold(
                    containerColor = SteelDark
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(SteelDark, SteelDeepInk)
                                )
                            )
                    ) {
                        NetconAppScreen(
                            viewModel = viewModel,
                            onRequestVpnPermission = {
                                val intent = VpnService.prepare(this@MainActivity)
                                if (intent != null) {
                                    vpnPermissionLauncher.launch(intent)
                                } else {
                                    viewModel.startVpnService(this@MainActivity)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NetconAppScreen(
    viewModel: VpnViewModel,
    onRequestVpnPermission: () -> Unit
) {
    val context = LocalContext.current

    val tab by viewModel.tab.collectAsStateWithLifecycle()
    val status by viewModel.vpnStatus.collectAsStateWithLifecycle()
    val selectedNode by viewModel.selectedNode.collectAsStateWithLifecycle()
    val nodes by viewModel.nodes.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
    val purpose by viewModel.purpose.collectAsStateWithLifecycle()
    val apiKey by viewModel.apiKey.collectAsStateWithLifecycle()
    val aiBusy by viewModel.aiBusy.collectAsStateWithLifecycle()
    val recommendation by viewModel.recommendation.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()

    val showWelcome by viewModel.showWelcome.collectAsStateWithLifecycle()
    val showServerManager by viewModel.showServerManager.collectAsStateWithLifecycle()
    val showLanguageSelector by viewModel.showLanguageSelector.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Chassis Frame Outer Border
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(SteelBase)
                .border(2.dp, SteelTrayBevel, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "NETCON",
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = CopperHighlight,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "PRECISION CONNECTION SYSTEM",
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }

                // Status LED Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SteelDeepInk)
                        .border(1.dp, SteelBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                when (status) {
                                    VpnStatus.CONNECTED -> LedGreen
                                    VpnStatus.CONNECTING -> LedYellow
                                    VpnStatus.OFFLINE -> LedRed
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (status) {
                            VpnStatus.CONNECTED -> "CONNECTED"
                            VpnStatus.CONNECTING -> "CONNECTING"
                            VpnStatus.OFFLINE -> "OFFLINE"
                        },
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = when (status) {
                            VpnStatus.CONNECTED -> LedGreen
                            VpnStatus.CONNECTING -> LedYellow
                            VpnStatus.OFFLINE -> LedRed
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pill Tab Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SteelDeepInk)
                    .border(1.dp, SteelBorder, RoundedCornerShape(24.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavTabButton(
                    label = "CONNECTION",
                    icon = Icons.Default.Bolt,
                    selected = tab == AppTab.CONNECTION,
                    onClick = { viewModel.setTab(AppTab.CONNECTION) },
                    modifier = Modifier.weight(1f).testTag("tab_connection")
                )
                NavTabButton(
                    label = "CONFIG",
                    icon = Icons.Default.Settings,
                    selected = tab == AppTab.SETTINGS,
                    onClick = { viewModel.setTab(AppTab.SETTINGS) },
                    modifier = Modifier.weight(1f).testTag("tab_config")
                )
                NavTabButton(
                    label = "INFO",
                    icon = Icons.Default.Info,
                    selected = tab == AppTab.INFO,
                    onClick = { viewModel.setTab(AppTab.INFO) },
                    modifier = Modifier.weight(1f).testTag("tab_info")
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tab Content Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Crossfade(targetState = tab, label = "TabSwitch") { currentTab ->
                    when (currentTab) {
                        AppTab.CONNECTION -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    AiTrayPanel(
                                        purpose = purpose,
                                        onPurposeChange = { viewModel.setPurpose(it) },
                                        apiKey = apiKey,
                                        onApiKeyChange = { viewModel.setApiKey(it) },
                                        aiBusy = aiBusy,
                                        onAskAi = { viewModel.askAi() },
                                        recommendation = recommendation,
                                        error = error,
                                        onApplyRecommendation = { recId ->
                                            nodes.find { it.id == recId }?.let { viewModel.selectNode(it) }
                                        }
                                    )

                                    ActiveServerCard(
                                        node = selectedNode,
                                        onOpenManager = { viewModel.toggleServerManager(true) }
                                    )
                                }

                                // Live Instrument Control Deck
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(SteelTraySurface)
                                        .border(1.dp, SteelBorder, RoundedCornerShape(14.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Gauge(
                                            label = "LATENCY PING",
                                            value = telemetry.ping.toFloat(),
                                            unit = "ms",
                                            max = 200f,
                                            active = status == VpnStatus.CONNECTED,
                                            modifier = Modifier.weight(1f)
                                        )

                                        PowerControl(
                                            status = status,
                                            duration = telemetry.connectedDuration,
                                            onClick = {
                                                viewModel.toggleVpn(context, prepareIntentLauncher = onRequestVpnPermission)
                                            },
                                            modifier = Modifier.weight(1.2f)
                                        )

                                        Gauge(
                                            label = "SPEED RATE",
                                            value = telemetry.speed,
                                            unit = "Mbps",
                                            max = 100f,
                                            active = status == VpnStatus.CONNECTED,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Telemetry Strip
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SteelDeepInk)
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceAround,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TelemetryItem(
                                            label = "DOWNLOAD",
                                            icon = Icons.Default.ArrowDownward,
                                            value = "${String.format("%.1f", telemetry.down)} Mbps"
                                        )
                                        TelemetryItem(
                                            label = "UPLOAD",
                                            icon = Icons.Default.ArrowUpward,
                                            value = "${String.format("%.1f", telemetry.up)} Mbps"
                                        )
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("RECEIVED", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                                            OdometerView(valueMb = telemetry.received)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("SENT", fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                                            OdometerView(valueMb = telemetry.sent)
                                        }
                                    }
                                }

                                // Connection Footer
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("ZERO LOGS. ZERO COMPROMISE.", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Lock, contentDescription = null, tint = CopperAccent, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("LOCAL CONFIG ENGINE", fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
                                    }
                                }
                            }
                        }

                        AppTab.SETTINGS -> {
                            SettingsPanel(
                                settings = settings,
                                onSettingsChange = { viewModel.updateSettings(it) },
                                activeNode = selectedNode
                            )
                        }

                        AppTab.INFO -> {
                            InfoPanel(
                                node = selectedNode,
                                connected = status == VpnStatus.CONNECTED,
                                onOpenWelcome = { viewModel.toggleWelcome(true) },
                                onOpenLanguage = { viewModel.toggleLanguageSelector(true) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chassis Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NETCON / ENGINEERED FOR PRIVACY",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = "XRAY CORE / NATIVE EDITION",
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Subtitle Below Chassis
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BRUSHED STEEL × BURNISHED COPPER · BUILD 2.4.1",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
        }
    }

    // Modal Drawers & Dialogs
    ServerManagerDrawer(
        open = showServerManager,
        onDismiss = { viewModel.toggleServerManager(false) },
        nodes = nodes,
        selectedId = selectedNode?.id ?: "",
        onSelect = {
            viewModel.selectNode(it)
            viewModel.toggleServerManager(false)
        },
        onDelete = { viewModel.deleteNode(it) },
        onAddLink = { viewModel.addConfigLink(it) },
        onRefreshPings = { viewModel.measureAllNodePings() }
    )

    WelcomeDialog(
        open = showWelcome,
        onDismiss = { viewModel.toggleWelcome(false) }
    )

    LanguageSelector(
        open = showLanguageSelector,
        currentLang = language,
        onSelect = { viewModel.setLanguage(it) },
        onDismiss = { viewModel.toggleLanguageSelector(false) }
    )
}

@Composable
private fun NavTabButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) CopperAccent else SteelDeepInk,
            contentColor = if (selected) SteelDeepInk else TextSecondary
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.padding(horizontal = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TelemetryItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = CopperGlow, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text(label, fontSize = 8.sp, fontFamily = FontFamily.Monospace, color = TextSecondary)
        }
        Text(value, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = CopperGlow)
    }
}
