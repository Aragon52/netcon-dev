package com.example.util

object LanguageManager {

    enum class Language(val code: String, val label: String, val flag: String) {
        EN("en", "English", "🇺🇸"),
        ES("es", "Español", "🇪🇸"),
        ZH("zh", "中文", "🇨🇳"),
        FA("fa", "فارسی", "🇮🇷"),
        RU("ru", "Русский", "🇷🇺"),
        DE("de", "Deutsch", "🇩🇪"),
        FR("fr", "Français", "🇫🇷")
    }

    private val translations = mapOf(
        "CONNECTED" to mapOf(
            "en" to "CONNECTED", "es" to "CONECTADO", "zh" to "已连接",
            "fa" to "متصل شد", "ru" to "ПОДКЛЮЧЕНО", "de" to "VERBUNDEN", "fr" to "CONNECTÉ"
        ),
        "CONNECTING" to mapOf(
            "en" to "CONNECTING", "es" to "CONECTANDO", "zh" to "连接中",
            "fa" to "در حال اتصال", "ru" to "ПОДКЛЮЧЕНИЕ", "de" to "VERBINDEN", "fr" to "CONNEXION"
        ),
        "OFFLINE" to mapOf(
            "en" to "OFFLINE", "es" to "DESCONECTADO", "zh" to "未连接",
            "fa" to "آفلاین", "ru" to "ОФЛАЙН", "de" to "OFFLINE", "fr" to "HORS LIGNE"
        ),
        "CONNECTION" to mapOf(
            "en" to "CONNECTION", "es" to "CONEXIÓN", "zh" to "连接",
            "fa" to "اتصال", "ru" to "СВЯЗЬ", "de" to "VERBINDUNG", "fr" to "CONNEXION"
        ),
        "CONFIG" to mapOf(
            "en" to "CONFIG", "es" to "AJUSTES", "zh" to "配置",
            "fa" to "تنظیمات", "ru" to "НАСТРОЙКИ", "de" to "KONFIG", "fr" to "CONFIG"
        ),
        "INFO" to mapOf(
            "en" to "INFO", "es" to "INFO", "zh" to "信息",
            "fa" to "اطلاعات", "ru" to "ИНФО", "de" to "INFO", "fr" to "INFO"
        ),
        "WHAT DO YOU WANT TO USE THE VPN FOR?" to mapOf(
            "en" to "WHAT DO YOU WANT TO USE THE VPN FOR?",
            "es" to "¿PARA QUÉ QUIERES USAR LA VPN?",
            "zh" to "您想用 VPN 做什么？",
            "fa" to "می‌خواهید از وی‌پی‌ان برای چه کاری استفاده کنید؟",
            "ru" to "ДЛЯ ЧЕГО ВАМ НУЖЕН VPN?",
            "de" to "WOFÜR MÖCHTEN SIE DAS VPN NUTZEN?",
            "fr" to "POURQUOI VOULEZ-VOUS UTILISER LE VPN ?"
        ),
        "MANAGE SERVERS" to mapOf(
            "en" to "MANAGE SERVERS", "es" to "GESTIONAR SERVIDORES", "zh" to "管理服务器",
            "fa" to "مدیریت سرورها", "ru" to "СЕРВЕРЫ", "de" to "SERVER VERWALTEN", "fr" to "GÉRER LES SERVEURS"
        ),
        "TAP TO CONNECT" to mapOf(
            "en" to "TAP TO CONNECT", "es" to "TOCA PARA CONECTAR", "zh" to "点击连接",
            "fa" to "لمس برای اتصال", "ru" to "НАЖМИТЕ ДЛЯ СВЯЗИ", "de" to "TIPPEN ZUM VERBINDEN", "fr" to "APPUYER POUR CONNECTER"
        ),
        "SECURE HANDSHAKE" to mapOf(
            "en" to "SECURE HANDSHAKE", "es" to "ENLACE SEGURO", "zh" to "安全握手",
            "fa" to "دست‌دادن امن", "ru" to "ЗАЩИЩЕННОЕ СОЕДИНЕНИЕ", "de" to "SICHERER HANDSHAKE", "fr" to "HANDSHAKE SÉCURISÉ"
        ),
        "TAP TO DISCONNECT" to mapOf(
            "en" to "TAP TO DISCONNECT", "es" to "TOCA PARA DESCONECTAR", "zh" to "点击断开",
            "fa" to "لمس برای قطع اتصال", "ru" to "НАЖМИТЕ ДЛЯ ОТКЛЮЧЕНИЯ", "de" to "TIPPEN ZUM TRENNEN", "fr" to "APPUYER POUR DÉCONNECTER"
        ),
        "ZERO LOGS. ZERO COMPROMISE." to mapOf(
            "en" to "ZERO LOGS. ZERO COMPROMISE.",
            "es" to "CERO REGISTROS. CERO COMPROMISOS.",
            "zh" to "零日志，零妥协。",
            "fa" to "بدون ثبت لوگ. بدون تعادل ناامن.",
            "ru" to "НОЛЬ ЛОГОВ. НОЛЬ КОМПРОМИССОВ.",
            "de" to "KEINE LOGS. KEINE KOMPROMISSE.",
            "fr" to "ZÉRO JOURNAL. ZÉRO COMPROMIS."
        )
    )

    fun translate(key: String, lang: Language): String {
        val langMap = translations[key] ?: return key
        return langMap[lang.code] ?: key
    }
}
