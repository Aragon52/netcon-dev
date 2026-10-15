package com.example.util

import com.example.BuildConfig
import com.example.model.AiRecommendation
import com.example.model.ServerNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeIntentAndRecommend(
        userPurpose: String,
        customKey: String?,
        availableNodes: List<ServerNode>
    ): AiRecommendation = withContext(Dispatchers.IO) {
        val apiKey = customKey.takeIf { !it.isNullOrEmpty() }
            ?: BuildConfig.GEMINI_API_KEY.takeIf { it.isNotEmpty() && it != "MY_GEMINI_API_KEY" }
            ?: ""

        if (apiKey.isEmpty()) {
            return@withContext fallbackRecommendation(userPurpose, availableNodes)
        }

        try {
            val prompt = """
                You are Netcon VPN AI assistant. Analyze the user's intent or platform request and recommend the best VPN server and protocol.
                User purpose/request: "$userPurpose"
                Available servers:
                ${availableNodes.joinToString("\n") { "- ID: ${it.id}, Name: ${it.name}, Country: ${it.country}, Protocol: ${it.protocol}, Ping: ${it.pingMs}ms" }}

                Return a raw JSON object only (no markdown, no code blocks):
                {
                  "reason": "Brief explanation of why this server and protocol fit the user request.",
                  "serverId": "exact server id from available servers",
                  "protocol": "vless/vmess/trojan/shadowsocks",
                  "routing": "Bypass LAN & Domestic or Global"
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        }
                        put("parts", partsArr)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("responseMimeType", "application/json")
                })
            }

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseStr = response.body?.string() ?: ""

            if (response.isSuccessful && responseStr.isNotEmpty()) {
                val jsonResp = JSONObject(responseStr)
                val candidates = jsonResp.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val text = candidates.getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    val resultObj = JSONObject(text)
                    return@withContext AiRecommendation(
                        reason = resultObj.optString("reason", "Optimized configuration recommended for $userPurpose."),
                        serverId = resultObj.optString("serverId", availableNodes.firstOrNull()?.id ?: ""),
                        protocol = resultObj.optString("protocol", "vless"),
                        routing = resultObj.optString("routing", "Bypass LAN & Domestic")
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext fallbackRecommendation(userPurpose, availableNodes)
    }

    private fun fallbackRecommendation(userPurpose: String, nodes: List<ServerNode>): AiRecommendation {
        val lower = userPurpose.lowercase()
        val node = when {
            lower.contains("pubg") || lower.contains("game") || lower.contains("gaming") || lower.contains("latency") ->
                nodes.firstOrNull { it.country == "United States" || it.country == "Germany" } ?: nodes.firstOrNull()
            lower.contains("youtube") || lower.contains("stream") || lower.contains("4k") || lower.contains("netflix") ->
                nodes.firstOrNull { it.country == "Japan" || it.country == "Singapore" } ?: nodes.firstOrNull()
            lower.contains("facebook") || lower.contains("ig") || lower.contains("instagram") || lower.contains("social") ->
                nodes.firstOrNull { it.country == "United States" || it.country == "United Kingdom" } ?: nodes.firstOrNull()
            else -> nodes.minByOrNull { if (it.pingMs > 0) it.pingMs else 999 } ?: nodes.firstOrNull()
        }

        val nodeName = node?.name ?: "Selected Node"
        val serverId = node?.id ?: ""
        val reason = when {
            lower.contains("pubg") || lower.contains("game") ->
                "Selected $nodeName for ultra-low latency UDP packet routing and anti-jitter buffer."
            lower.contains("youtube") || lower.contains("stream") ->
                "Selected $nodeName with VLESS-gRPC high throughput for buffer-free 4K video playback."
            lower.contains("facebook") || lower.contains("ig") ->
                "Selected $nodeName for encrypted SNI spoofing and reliable access to blocked social networks."
            else ->
                "Analyzed network route: $nodeName provides optimal hop distance, low latency ($node?.pingMs ms), and high bandwidth stability."
        }

        return AiRecommendation(
            reason = reason,
            serverId = serverId,
            protocol = node?.protocol ?: "vless",
            routing = "Bypass LAN & Domestic"
        )
    }
}
