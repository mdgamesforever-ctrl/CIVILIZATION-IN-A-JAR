package com.example.data

enum class AchievementCategory(val displayName: String, val icon: String) {
    ERA("Ages of Progress", "⏳"),
    POPULATION("Civilization Scale", "👥"),
    FOSSIL_DUST("Paleontology", "🦴"),
    PRESTIGE("Great Resets", "⚡"),
    DISCOVERY("Jar Biomes", "🏺")
}

data class AchievementDef(
    val id: String,
    val title: String,
    val description: String,
    val category: AchievementCategory,
    val icon: String,
    val rewardFossilDust: Double = 0.0
)

object AchievementCatalog {
    val ACHIEVEMENTS: List<AchievementDef> = listOf(
        // Era Milestones
        AchievementDef(
            id = "ach_era_2",
            title = "First Glimmer of Life",
            description = "Evolve to Era 2: First Life",
            category = AchievementCategory.ERA,
            icon = "🧬",
            rewardFossilDust = 10.0
        ),
        AchievementDef(
            id = "ach_era_3",
            title = "The Hearth Spark",
            description = "Evolve to Era 3: Tribal Age",
            category = AchievementCategory.ERA,
            icon = "🔥",
            rewardFossilDust = 25.0
        ),
        AchievementDef(
            id = "ach_era_5",
            title = "Metropolis of Glass",
            description = "Reached Era 5: City Age",
            category = AchievementCategory.ERA,
            icon = "🏛️",
            rewardFossilDust = 100.0
        ),
        AchievementDef(
            id = "ach_era_7",
            title = "Silicon Grid",
            description = "Reached Era 7: Digital Age",
            category = AchievementCategory.ERA,
            icon = "🤖",
            rewardFossilDust = 500.0
        ),
        AchievementDef(
            id = "ach_era_8",
            title = "Touching the Lid",
            description = "Reached Era 8: Space Age",
            category = AchievementCategory.ERA,
            icon = "🚀",
            rewardFossilDust = 2000.0
        ),
        AchievementDef(
            id = "ach_era_9",
            title = "Cosmic Rapture",
            description = "Ascended to Era 9: Singularity & Ascension",
            category = AchievementCategory.ERA,
            icon = "✨",
            rewardFossilDust = 10000.0
        ),

        // Population Milestones
        AchievementDef(
            id = "ach_pop_1k",
            title = "Microscopic Clan",
            description = "Reach a peak population of 1,000 organisms",
            category = AchievementCategory.POPULATION,
            icon = "🌱",
            rewardFossilDust = 5.0
        ),
        AchievementDef(
            id = "ach_pop_1m",
            title = "Million Souls in Glass",
            description = "Reach a peak population of 1,000,000",
            category = AchievementCategory.POPULATION,
            icon = "🏙️",
            rewardFossilDust = 50.0
        ),
        AchievementDef(
            id = "ach_pop_1b",
            title = "Billion Inhabitants",
            description = "Reach a peak population of 1 Billion",
            category = AchievementCategory.POPULATION,
            icon = "🌐",
            rewardFossilDust = 500.0
        ),
        AchievementDef(
            id = "ach_pop_1t",
            title = "Trillion Cosmic Entities",
            description = "Reach a peak population of 1 Trillion",
            category = AchievementCategory.POPULATION,
            icon = "🌌",
            rewardFossilDust = 5000.0
        ),

        // Fossil Dust Milestones
        AchievementDef(
            id = "ach_fd_100",
            title = "Fossil Sifter",
            description = "Accumulate 100 Fossil Dust",
            category = AchievementCategory.FOSSIL_DUST,
            icon = "⛏️",
            rewardFossilDust = 15.0
        ),
        AchievementDef(
            id = "ach_fd_10k",
            title = "Master Paleontologist",
            description = "Accumulate 10,000 Fossil Dust",
            category = AchievementCategory.FOSSIL_DUST,
            icon = "🏺",
            rewardFossilDust = 250.0
        ),
        AchievementDef(
            id = "ach_fd_1m",
            title = "Dust Millionaire",
            description = "Accumulate 1,000,000 Lifetime Fossil Dust",
            category = AchievementCategory.FOSSIL_DUST,
            icon = "💎",
            rewardFossilDust = 25000.0
        ),

        // Great Reset / Extinction Milestones
        AchievementDef(
            id = "ach_reset_1",
            title = "First Rebirth",
            description = "Complete 1 Great Reset Extinction event",
            category = AchievementCategory.PRESTIGE,
            icon = "⚡",
            rewardFossilDust = 20.0
        ),
        AchievementDef(
            id = "ach_reset_5",
            title = "Cycle of Eras",
            description = "Complete 5 Great Reset Extinction events",
            category = AchievementCategory.PRESTIGE,
            icon = "🔄",
            rewardFossilDust = 500.0
        ),

        // Multi-Jar Biomes
        AchievementDef(
            id = "ach_unlock_spice",
            title = "Desert Bloom",
            description = "Unlock the Cumin Spice Jar",
            category = AchievementCategory.DISCOVERY,
            icon = "🌶️",
            rewardFossilDust = 50.0
        ),
        AchievementDef(
            id = "ach_unlock_terrarium",
            title = "Living Eden",
            description = "Unlock the Botanical Terrarium",
            category = AchievementCategory.DISCOVERY,
            icon = "🌿",
            rewardFossilDust = 5000.0
        )
    )

    fun getById(id: String): AchievementDef? = ACHIEVEMENTS.find { it.id == id }
}
