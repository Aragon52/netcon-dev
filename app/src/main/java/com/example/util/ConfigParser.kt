package com.example.util

import android.util.Base64
import com.example.model.ServerNode
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.net.URLDecoder
import java.util.UUID

object ConfigParser {

    /**
     * Parses a raw config string (URI link or multiple links) into ServerNode list
     */
    fun parseConfig(input: String): List<ServerNode> {
        val lines = input.trim().split("\n")
        val nodes = mutableListOf<ServerNode>()

        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.isEmpty()) continue

            try {
                when {
                    trimmed.startsWith("vmess://") -> parseVmess(trimmed)?.let { nodes.add(it) }
                    trimmed.startsWith("vless://") -> parseVless(trimmed)?.let { nodes.add(it) }
                    trimmed.startsWith("trojan://") -> parseTrojan(trimmed)?.let { nodes.add(it) }
                    trimmed.startsWith("ss://") -> parseShadowsocks(trimmed)?.let { nodes.add(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return nodes
    }

    private fun parseVmess(uri: String): ServerNode? {
        val rawBase64 = uri.removePrefix("vmess://")
        val decoded = String(Base64.decode(rawBase64, Base64.NO_WRAP or Base64.URL_SAFE))
        val json = JSONObject(decoded)

        val name = json.optString("ps", "VMess Node")
        val address = json.optString("add", "127.0.0.1")
        val port = json.optInt("port", 443)
        val id = json.optString("id", UUID.randomUUID().toString())
        val security = json.optString("tls", "tls")
        val type = json.optString("net", "ws")

        return ServerNode(
            id = UUID.randomUUID().toString(),
            name = name,
            country = extractCountryFromName(name),
            flagEmoji = getFlagEmoji(extractCountryFromName(name)),
            address = address,
            port = port,
            protocol = "vmess",
            rawConfig = uri,
            uuid = id,
            security = if (security == "none") "none" else "tls",
            type = type
        )
    }

    private fun parseVless(uriStr: String): ServerNode? {
        val uri = URI(uriStr)
        val userInfo = uri.userInfo ?: ""
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port != -1) uri.port else 443
        val query = uri.rawQuery ?: ""
        val fragment = uri.rawFragment?.let { URLDecoder.decode(it, "UTF-8") } ?: "VLESS Node"

        val queryParams = parseQueryParams(query)
        val security = queryParams["security"] ?: "tls"
        val type = queryParams["type"] ?: "ws"

        return ServerNode(
            id = UUID.randomUUID().toString(),
            name = fragment,
            country = extractCountryFromName(fragment),
            flagEmoji = getFlagEmoji(extractCountryFromName(fragment)),
            address = host,
            port = port,
            protocol = "vless",
            rawConfig = uriStr,
            uuid = userInfo,
            security = security,
            type = type
        )
    }

    private fun parseTrojan(uriStr: String): ServerNode? {
        val uri = URI(uriStr)
        val password = uri.userInfo ?: ""
        val host = uri.host ?: "127.0.0.1"
        val port = if (uri.port != -1) uri.port else 443
        val query = uri.rawQuery ?: ""
        val fragment = uri.rawFragment?.let { URLDecoder.decode(it, "UTF-8") } ?: "Trojan Node"

        val queryParams = parseQueryParams(query)
        val type = queryParams["type"] ?: "tcp"

        return ServerNode(
            id = UUID.randomUUID().toString(),
            name = fragment,
            country = extractCountryFromName(fragment),
            flagEmoji = getFlagEmoji(extractCountryFromName(fragment)),
            address = host,
            port = port,
            protocol = "trojan",
            rawConfig = uriStr,
            uuid = password,
            security = "tls",
            type = type
        )
    }

    private fun parseShadowsocks(uriStr: String): ServerNode? {
        var clean = uriStr.removePrefix("ss://")
        var fragment = "Shadowsocks Node"
        if (clean.contains("#")) {
            val parts = clean.split("#", limit = 2)
            clean = parts[0]
            fragment = URLDecoder.decode(parts[1], "UTF-8")
        }

        var host = "127.0.0.1"
        var port = 8388
        var password = ""

        if (clean.contains("@")) {
            val mainParts = clean.split("@", limit = 2)
            val userInfo = mainParts[0]
            val hostPort = mainParts[1]

            val decodedUserInfo = try {
                String(Base64.decode(userInfo, Base64.NO_WRAP or Base64.URL_SAFE))
            } catch (e: Exception) {
                userInfo
            }
            password = decodedUserInfo

            if (hostPort.contains(":")) {
                val hp = hostPort.split(":", limit = 2)
                host = hp[0]
                port = hp[1].toIntOrNull() ?: 8388
            } else {
                host = hostPort
            }
        }

        return ServerNode(
            id = UUID.randomUUID().toString(),
            name = fragment,
            country = extractCountryFromName(fragment),
            flagEmoji = getFlagEmoji(extractCountryFromName(fragment)),
            address = host,
            port = port,
            protocol = "shadowsocks",
            rawConfig = uriStr,
            uuid = password
        )
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        if (query.isEmpty()) return map
        for (param in query.split("&")) {
            val pair = param.split("=", limit = 2)
            if (pair.size == 2) {
                map[pair[0]] = URLDecoder.decode(pair[1], "UTF-8")
            }
        }
        return map
    }

    fun extractCountryFromName(name: String): String {
        val upper = name.uppercase()
        return when {
            upper.contains("US") || upper.contains("UNITED STATES") || upper.contains("AMERICA") -> "United States"
            upper.contains("DE") || upper.contains("GERMANY") || upper.contains("FRANKFURT") -> "Germany"
            upper.contains("JP") || upper.contains("JAPAN") || upper.contains("TOKYO") -> "Japan"
            upper.contains("SG") || upper.contains("SINGAPORE") -> "Singapore"
            upper.contains("UK") || upper.contains("GB") || upper.contains("LONDON") -> "United Kingdom"
            upper.contains("NL") || upper.contains("NETHERLANDS") || upper.contains("AMSTERDAM") -> "Netherlands"
            upper.contains("HK") || upper.contains("HONG KONG") -> "Hong Kong"
            upper.contains("CA") || upper.contains("CANADA") -> "Canada"
            upper.contains("FR") || upper.contains("FRANCE") -> "France"
            upper.contains("KR") || upper.contains("KOREA") -> "South Korea"
            else -> "Global"
        }
    }

    fun getFlagEmoji(country: String): String {
        return when (country) {
            "United States" -> "🇺🇸"
            "Germany" -> "🇩🇪"
            "Japan" -> "🇯🇵"
            "Singapore" -> "🇸🇬"
            "United Kingdom" -> "🇬🇧"
            "Netherlands" -> "🇳🇱"
            "Hong Kong" -> "🇭🇰"
            "Canada" -> "🇨🇦"
            "France" -> "🇫🇷"
            "South Korea" -> "🇰🇷"
            else -> "🌐"
        }
    }

    /**
     * Generates a standard Xray / V2Ray JSON configuration string
     */
    fun generateXrayConfigJson(node: ServerNode?, dns: String, routingRule: String): String {
        val root = JSONObject()

        // Log
        val log = JSONObject()
        log.put("loglevel", "warning")
        root.put("log", log)

        // Inbounds
        val inbounds = JSONArray()

        val socksInbound = JSONObject()
        socksInbound.put("port", 10808)
        socksInbound.put("listen", "127.0.0.1")
        socksInbound.put("protocol", "socks")
        val socksSettings = JSONObject()
        socksSettings.put("udp", true)
        socksInbound.put("settings", socksSettings)
        inbounds.put(socksInbound)

        val httpInbound = JSONObject()
        httpInbound.put("port", 10809)
        httpInbound.put("listen", "127.0.0.1")
        httpInbound.put("protocol", "http")
        inbounds.put(httpInbound)

        root.put("inbounds", inbounds)

        // Outbounds
        val outbounds = JSONArray()
        val proxyOutbound = JSONObject()
        proxyOutbound.put("tag", "proxy")
        proxyOutbound.put("protocol", node?.protocol ?: "vless")

        val outboundSettings = JSONObject()
        val vnext = JSONArray()
        val serverObj = JSONObject()
        serverObj.put("address", node?.address ?: "127.0.0.1")
        serverObj.put("port", node?.port ?: 443)

        val users = JSONArray()
        val userObj = JSONObject()
        userObj.put("id", if (node?.uuid.isNullOrEmpty()) "00000000-0000-0000-0000-000000000000" else node?.uuid)
        userObj.put("encryption", "none")
        users.put(userObj)
        serverObj.put("users", users)

        vnext.put(serverObj)
        outboundSettings.put("vnext", vnext)
        proxyOutbound.put("settings", outboundSettings)

        outbounds.put(proxyOutbound)

        val directOutbound = JSONObject()
        directOutbound.put("tag", "direct")
        directOutbound.put("protocol", "freedom")
        outbounds.put(directOutbound)

        root.put("outbounds", outbounds)

        // DNS
        val dnsObj = JSONObject()
        val serversArr = JSONArray()
        val cleanDns = dns.split(" ")[0]
        serversArr.put(cleanDns)
        serversArr.put("8.8.8.8")
        dnsObj.put("servers", serversArr)
        root.put("dns", dnsObj)

        return root.toString(2)
    }

    val defaultPresetNodes = listOf(
        ServerNode(
            id = "preset-us-01",
            name = "US-East-1 · Ultra Low Latency",
            country = "United States",
            flagEmoji = "🇺🇸",
            address = "us-east.netcon-vpn.net",
            port = 443,
            protocol = "vless",
            pingMs = 38,
            uuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            security = "tls",
            type = "ws"
        ),
        ServerNode(
            id = "preset-de-01",
            name = "DE-Frankfurt-02 · Gaming Optimized",
            country = "Germany",
            flagEmoji = "🇩🇪",
            address = "de-fra.netcon-vpn.net",
            port = 443,
            protocol = "vmess",
            pingMs = 45,
            uuid = "b2c3d4e5-f6a1-7890-abcd-ef1234567890",
            security = "tls",
            type = "grpc"
        ),
        ServerNode(
            id = "preset-jp-01",
            name = "JP-Tokyo-01 · Fast Streaming 4K",
            country = "Japan",
            flagEmoji = "🇯🇵",
            address = "jp-tyo.netcon-vpn.net",
            port = 443,
            protocol = "trojan",
            pingMs = 52,
            uuid = "c3d4e5f6-a1b2-7890-abcd-ef1234567890",
            security = "tls",
            type = "tcp"
        ),
        ServerNode(
            id = "preset-sg-01",
            name = "SG-Singapore-03 · High Speed Node",
            country = "Singapore",
            flagEmoji = "🇸🇬",
            address = "sg-sin.netcon-vpn.net",
            port = 8388,
            protocol = "shadowsocks",
            pingMs = 61,
            uuid = "d4e5f6a1-b2c3-7890-abcd-ef1234567890"
        ),
        ServerNode(
            id = "preset-uk-01",
            name = "UK-London-01 · Censorship Bypass",
            country = "United Kingdom",
            flagEmoji = "🇬🇧",
            address = "uk-lon.netcon-vpn.net",
            port = 443,
            protocol = "vless",
            pingMs = 74,
            uuid = "e5f6a1b2-c3d4-7890-abcd-ef1234567890"
        )
    )
}
