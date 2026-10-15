package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AiRecommendation
import com.example.model.AppTab
import com.example.model.ServerNode
import com.example.model.TelemetryData
import com.example.model.VpnSettings
import com.example.model.VpnStatus
import com.example.service.NetconVpnService
import com.example.util.ConfigParser
import com.example.util.GeminiAiService
import com.example.util.LanguageManager
import com.example.util.PingUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VpnViewModel : ViewModel() {

    private val _tab = MutableStateFlow(AppTab.CONNECTION)
    val tab: StateFlow<AppTab> = _tab.asStateFlow()

    private val _nodes = MutableStateFlow(ConfigParser.defaultPresetNodes)
    val nodes: StateFlow<List<ServerNode>> = _nodes.asStateFlow()

    private val _selectedNode = MutableStateFlow<ServerNode?>(ConfigParser.defaultPresetNodes.first())
    val selectedNode: StateFlow<ServerNode?> = _selectedNode.asStateFlow()

    private val _settings = MutableStateFlow(VpnSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    val vpnStatus: StateFlow<VpnStatus> = NetconVpnService.vpnStatus

    private val _telemetry = MutableStateFlow(TelemetryData())
    val telemetry: StateFlow<TelemetryData> = _telemetry.asStateFlow()

    private val _purpose = MutableStateFlow("")
    val purpose: StateFlow<String> = _purpose.asStateFlow()

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _aiBusy = MutableStateFlow(false)
    val aiBusy: StateFlow<Boolean> = _aiBusy.asStateFlow()

    private val _recommendation = MutableStateFlow<AiRecommendation?>(null)
    val recommendation: StateFlow<AiRecommendation?> = _recommendation.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _language = MutableStateFlow(LanguageManager.Language.EN)
    val language: StateFlow<LanguageManager.Language> = _language.asStateFlow()

    private val _showWelcome = MutableStateFlow(false)
    val showWelcome: StateFlow<Boolean> = _showWelcome.asStateFlow()

    private val _showServerManager = MutableStateFlow(false)
    val showServerManager: StateFlow<Boolean> = _showServerManager.asStateFlow()

    private val _showAddServerDialog = MutableStateFlow(false)
    val showAddServerDialog: StateFlow<Boolean> = _showAddServerDialog.asStateFlow()

    private val _showLanguageSelector = MutableStateFlow(false)
    val showLanguageSelector: StateFlow<Boolean> = _showLanguageSelector.asStateFlow()

    private var durationSeconds = 0
    private var timerJob: Job? = null

    init {
        observeServiceTelemetry()
        measureAllNodePings()
    }

    private fun observeServiceTelemetry() {
        viewModelScope.launch {
            NetconVpnService.vpnStatus.collect { status ->
                if (status == VpnStatus.CONNECTED) {
                    startTimer()
                } else if (status == VpnStatus.OFFLINE) {
                    stopTimer()
                }
            }
        }

        viewModelScope.launch {
            NetconVpnService.currentSpeed.collect { speed ->
                updateTelemetry { copy(speed = speed) }
            }
        }

        viewModelScope.launch {
            NetconVpnService.currentDown.collect { down ->
                updateTelemetry { copy(down = down) }
            }
        }

        viewModelScope.launch {
            NetconVpnService.currentUp.collect { up ->
                updateTelemetry { copy(up = up) }
            }
        }

        viewModelScope.launch {
            NetconVpnService.bytesRx.collect { bytes ->
                val mb = bytes / 1000000f
                updateTelemetry { copy(received = mb) }
            }
        }

        viewModelScope.launch {
            NetconVpnService.bytesTx.collect { bytes ->
                val mb = bytes / 1000000f
                updateTelemetry { copy(sent = mb) }
            }
        }
    }

    private fun updateTelemetry(transform: TelemetryData.() -> TelemetryData) {
        _telemetry.value = _telemetry.value.transform()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (vpnStatus.value == VpnStatus.CONNECTED) {
                delay(1000)
                durationSeconds++
                val hours = durationSeconds / 3600
                val minutes = (durationSeconds % 3600) / 60
                val seconds = durationSeconds % 60
                val formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                updateTelemetry { copy(connectedDuration = formatted) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        durationSeconds = 0
        updateTelemetry { copy(connectedDuration = "00:00:00", speed = 0f, down = 0f, up = 0f) }
    }

    fun setTab(tab: AppTab) {
        _tab.value = tab
    }

    fun setPurpose(text: String) {
        _purpose.value = text
    }

    fun setApiKey(key: String) {
        _apiKey.value = key
    }

    fun setLanguage(lang: LanguageManager.Language) {
        _language.value = lang
    }

    fun toggleWelcome(show: Boolean) {
        _showWelcome.value = show
    }

    fun toggleServerManager(show: Boolean) {
        _showServerManager.value = show
    }

    fun toggleAddServerDialog(show: Boolean) {
        _showAddServerDialog.value = show
    }

    fun toggleLanguageSelector(show: Boolean) {
        _showLanguageSelector.value = show
    }

    fun selectNode(node: ServerNode) {
        _selectedNode.value = node
        updateTelemetry { copy(ping = if (node.pingMs > 0) node.pingMs else 42) }
    }

    fun toggleVpn(context: Context, prepareIntentLauncher: (() -> Unit)? = null) {
        when (vpnStatus.value) {
            VpnStatus.OFFLINE -> {
                val prepareIntent = VpnService.prepare(context)
                if (prepareIntent != null) {
                    prepareIntentLauncher?.invoke()
                } else {
                    startVpnService(context)
                }
            }
            VpnStatus.CONNECTING, VpnStatus.CONNECTED -> {
                stopVpnService(context)
            }
        }
    }

    fun startVpnService(context: Context) {
        val node = _selectedNode.value ?: return
        val intent = Intent(context, NetconVpnService::class.java).apply {
            putExtra(NetconVpnService.EXTRA_SERVER_NAME, node.name)
            putExtra(NetconVpnService.EXTRA_DNS, _settings.value.dnsServer)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stopVpnService(context: Context) {
        val intent = Intent(context, NetconVpnService::class.java).apply {
            action = NetconVpnService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun askAi() {
        if (_purpose.value.trim().isEmpty()) {
            _error.value = "Please describe your intent or paste a config link."
            return
        }

        // Check if input is direct raw config link
        if (_purpose.value.contains("://")) {
            val parsed = ConfigParser.parseConfig(_purpose.value)
            if (parsed.isNotEmpty()) {
                val newNodes = _nodes.value + parsed
                _nodes.value = newNodes
                selectNode(parsed.first())
                _recommendation.value = AiRecommendation(
                    reason = "Successfully parsed direct configuration link (${parsed.first().protocol.uppercase()}). Connected node updated.",
                    serverId = parsed.first().id,
                    protocol = parsed.first().protocol,
                    routing = _settings.value.routingRule
                )
                _error.value = null
                return
            }
        }

        _aiBusy.value = true
        _error.value = null
        viewModelScope.launch {
            val rec = GeminiAiService.analyzeIntentAndRecommend(
                userPurpose = _purpose.value,
                customKey = _apiKey.value,
                availableNodes = _nodes.value
            )
            _recommendation.value = rec
            _aiBusy.value = false

            val recommendedNode = _nodes.value.find { it.id == rec.serverId }
            if (recommendedNode != null) {
                selectNode(recommendedNode)
            }
        }
    }

    fun addConfigLink(linkInput: String) {
        val parsed = ConfigParser.parseConfig(linkInput)
        if (parsed.isNotEmpty()) {
            _nodes.value = _nodes.value + parsed
            selectNode(parsed.first())
            _error.value = null
            _showAddServerDialog.value = false
        } else {
            _error.value = "Invalid or unsupported configuration format."
        }
    }

    fun deleteNode(nodeId: String) {
        _nodes.value = _nodes.value.filter { it.id != nodeId }
        if (_selectedNode.value?.id == nodeId) {
            _selectedNode.value = _nodes.value.firstOrNull()
        }
    }

    fun updateSettings(newSettings: VpnSettings) {
        _settings.value = newSettings
    }

    fun measureAllNodePings() {
        viewModelScope.launch {
            val updated = _nodes.value.map { node ->
                val ping = PingUtil.measurePing(node.address, node.port)
                node.copy(pingMs = ping)
            }
            _nodes.value = updated
            _selectedNode.value?.let { current ->
                val refreshed = updated.find { it.id == current.id }
                if (refreshed != null) {
                    _selectedNode.value = refreshed
                    updateTelemetry { copy(ping = refreshed.pingMs) }
                }
            }
        }
    }
}
