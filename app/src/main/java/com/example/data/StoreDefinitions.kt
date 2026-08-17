package com.example.data

import org.json.JSONArray
import org.json.JSONObject

data class GemPack(
    val productId: String,
    val gemAmount: Long,
    val title: String,
    val badge: String?,
    val defaultPriceFormatted: String,
    val iconEmoji: String = "💎",
    val description: String = ""
)

enum class GemPowerUpType {
    INSTANT_GROWTH_30M,
    INSTANT_GROWTH_2H,
    SPEED_UP_30M,
    EXTINCTION_INSURANCE,
    INSTANT_ERA_UNLOCK,
    COSMETIC_THEME
}

data class CosmeticJarTheme(
    val id: String,
    val name: String,
    val description: String,
    val gemCost: Long,
    val previewEmoji: String,
    val primaryColorHex: Long,
    val glowColorHex: Long
)

data class GemStoreItem(
    val id: String,
    val type: GemPowerUpType,
    val title: String,
    val description: String,
    val gemCost: Long,
    val iconEmoji: String,
    val themeId: String? = null,
    val badge: String? = null
)

object StoreDefinitions {
    // In-App Consumable Products for Google Play Billing
    val GEM_PACKS = listOf(
        GemPack(
            productId = "gems_pack_small_100",
            gemAmount = 100L,
            title = "Small Pouch of Gems",
            badge = "Starter",
            defaultPriceFormatted = "$0.99",
            iconEmoji = "💎",
            description = "100 pure crystallized gems for quick speed-ups"
        ),
        GemPack(
            productId = "gems_pack_medium_550",
            gemAmount = 550L,
            title = "Medium Gem Sieve",
            badge = "+10% Bonus",
            defaultPriceFormatted = "$4.99",
            iconEmoji = "💎",
            description = "500 + 50 bonus gems to catalyze your worlds"
        ),
        GemPack(
            productId = "gems_pack_large_1200",
            gemAmount = 1200L,
            title = "Large Gem Cache",
            badge = "+20% Bonus",
            defaultPriceFormatted = "$9.99",
            iconEmoji = "✨",
            description = "1,000 + 200 bonus gems for major technological leap"
        ),
        GemPack(
            productId = "gems_pack_mega_2500",
            gemAmount = 2500L,
            title = "Mega Singularity Vault",
            badge = "Best Value (+30%)",
            defaultPriceFormatted = "$19.99",
            iconEmoji = "👑",
            description = "2,000 + 500 bonus gems to master cosmic evolution"
        )
    )

    fun getPackByProductId(productId: String): GemPack? {
        return GEM_PACKS.find { it.productId == productId }
    }

    // Power-ups and items purchasable with in-game Gems
    val POWER_UP_ITEMS = listOf(
        GemStoreItem(
            id = "gem_boost_30m",
            type = GemPowerUpType.INSTANT_GROWTH_30M,
            title = "Instant Growth (30 Min)",
            description = "Instantly harvests and deposits 30 minutes of accumulated population growth & organic matter into your active jar.",
            gemCost = 40L,
            iconEmoji = "⚡",
            badge = "Instant"
        ),
        GemStoreItem(
            id = "gem_boost_2h",
            type = GemPowerUpType.INSTANT_GROWTH_2H,
            title = "Super Epoch Surge (2 Hours)",
            description = "Instantly injects 2 hours of massive civilizational growth into the active jar.",
            gemCost = 120L,
            iconEmoji = "⏳",
            badge = "Popular"
        ),
        GemStoreItem(
            id = "gem_speedup_30m",
            type = GemPowerUpType.SPEED_UP_30M,
            title = "Speed-Up Token (2x Growth)",
            description = "Overclocks biological metabolism: Doubles (2x) population growth rate across all jars for 30 minutes. Stacks duration!",
            gemCost = 60L,
            iconEmoji = "🚀",
            badge = "2x Boost"
        ),
        GemStoreItem(
            id = "gem_insurance",
            type = GemPowerUpType.EXTINCTION_INSURANCE,
            title = "Extinction Insurance Token",
            description = "Places an impenetrable kinetic barrier around your jar. Automatically blocks and absorbs the next random extinction event!",
            gemCost = 50L,
            iconEmoji = "🛡️",
            badge = "Protection"
        ),
        GemStoreItem(
            id = "gem_era_unlock",
            type = GemPowerUpType.INSTANT_ERA_UNLOCK,
            title = "Instant Era Catalyst",
            description = "Bypasses the population requirement for the next Era, jumpstarting civilizational evolution immediately.",
            gemCost = 150L,
            iconEmoji = "🌌",
            badge = "Catalyst"
        )
    )

    // Visual Jar Skins & Themes
    val COSMETIC_THEMES = listOf(
        CosmeticJarTheme(
            id = "default",
            name = "Classic Glass",
            description = "The original warm glass container with soothing ambient lighting.",
            gemCost = 0L,
            previewEmoji = "🏺",
            primaryColorHex = 0xFF141018,
            glowColorHex = 0xFF00E5FF
        ),
        CosmeticJarTheme(
            id = "theme_amber",
            name = "Amber Bioluminescence",
            description = "Radiant golden-amber crystal aura with flickering subterranean spark motes.",
            gemCost = 200L,
            previewEmoji = "🌟",
            primaryColorHex = 0xFF241404,
            glowColorHex = 0xFFFFB74D
        ),
        CosmeticJarTheme(
            id = "theme_cyber",
            name = "Neon Cyber-Void",
            description = "High-tech synthwave aesthetic with pulsing cyan and electric magenta grid matrix.",
            gemCost = 200L,
            previewEmoji = "⚡",
            primaryColorHex = 0xFF0A0E24,
            glowColorHex = 0xFF00E5FF
        ),
        CosmeticJarTheme(
            id = "theme_emerald",
            name = "Ancient Emerald Biosphere",
            description = "Verdant deep forest glow infused with primordial botanical spores.",
            gemCost = 200L,
            previewEmoji = "🌿",
            primaryColorHex = 0xFF081C10,
            glowColorHex = 0xFF69F0AE
        ),
        CosmeticJarTheme(
            id = "theme_solar",
            name = "Solar Plasma Core",
            description = "Blazing stellar orange and deep crimson plasma filaments orbiting the jar rim.",
            gemCost = 250L,
            previewEmoji = "🔥",
            primaryColorHex = 0xFF260D08,
            glowColorHex = 0xFFFF7043
        )
    )

    fun parseUnlockedThemes(json: String): Set<String> {
        val set = mutableSetOf("default")
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                set.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return set
    }

    fun serializeUnlockedThemes(themes: Set<String>): String {
        val array = JSONArray()
        themes.forEach { array.put(it) }
        return array.toString()
    }
}
