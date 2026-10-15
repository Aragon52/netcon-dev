package com.example.model

enum class VpnStatus {
    OFFLINE,
    CONNECTING,
    CONNECTED
}

data class ServerNode(
    val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val address: String,
    val port: Int,
    val protocol: String, // vless, vmess, trojan, shadowsocks
    var pingMs: Int = -1,
    val isUntrusted: Boolean = false,
    val rawConfig: String = "",
    val uuid: String = "",
    val security: String = "tls",
    val type: String = "ws"
)

data class VpnSettings(
    val killSwitch: Boolean = true,
    val dnsServer: String = "1.1.1.1 (Cloudflare)",
    val customDns: String = "",
    val selectedProtocols: List<String> = listOf("vless", "vmess", "trojan", "shadowsocks"),
    val routingRule: String = "Bypass LAN & Domestic", // Global, Bypass LAN, Bypass LAN & Domestic, Rule-based
    val socksPort: Int = 10808,
    val httpPort: Int = 10809,
    val autoConnectOnBoot: Boolean = false
)

data class TelemetryData(
    val ping: Int = 42,
    val speed: Float = 0f,
    val down: Float = 0f,
    val up: Float = 0f,
    val received: Float = 0f, // MB
    val sent: Float = 0f,     // MB
    val connectedDuration: String = "00:00:00"
)

data class AiRecommendation(
    val reason: String,
    val serverId: String,
    val protocol: String,
    val routing: String
)

enum class AppTab {
    CONNECTION,
    SETTINGS,
    INFO
}
