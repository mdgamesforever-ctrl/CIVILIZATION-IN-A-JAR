package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class JarType(
    val id: String,
    val displayName: String,
    val description: String,
    val unlockCostFD: Double,
    val growthMultiplier: Double,
    val resourceMultiplier: Double,
    val extinctionRiskMultiplier: Double,
    val fossilDustMultiplier: Double = 1.0,
    val emoji: String = "🏺",
    val biomeName: String = "Classic Cellar",
    val accentColorHex: Long = 0xFF90A4AE
) {
    MASON(
        id = "mason",
        displayName = "Grandma's Mason Jar",
        description = "The original discovery. Balanced growth and steady evolution with baseline fossil sedimentation.",
        unlockCostFD = 0.0,
        growthMultiplier = 1.0,
        resourceMultiplier = 1.0,
        extinctionRiskMultiplier = 1.0,
        fossilDustMultiplier = 1.00,
        emoji = "🏺",
        biomeName = "Classic Cellar",
        accentColorHex = 0xFF90A4AE
    ),
    SPICE(
        id = "spice",
        displayName = "Cumin Spice Jar",
        description = "Desert Biome. Highly mineralized sand accelerates fossilization with a +35% Fossil Dust bonus.",
        unlockCostFD = 250.0,
        growthMultiplier = 0.7,
        resourceMultiplier = 2.5,
        extinctionRiskMultiplier = 0.8,
        fossilDustMultiplier = 1.35,
        emoji = "🧂",
        biomeName = "Arid Dunes",
        accentColorHex = 0xFFFFB74D
    ),
    JAM(
        id = "jam",
        displayName = "Strawberry Jam Jar",
        description = "Swamp Biome. Hyper-fertile ancient sediment with 2.0x explosive growth and +75% Fossil Dust yield.",
        unlockCostFD = 1500.0,
        growthMultiplier = 2.0,
        resourceMultiplier = 0.8,
        extinctionRiskMultiplier = 1.5,
        fossilDustMultiplier = 1.75,
        emoji = "🍓",
        biomeName = "Sugar Swamp",
        accentColorHex = 0xFFFF4081
    ),
    AQUARIUM(
        id = "aquarium",
        displayName = "Glass Aquatic Bowl",
        description = "Marine Biome. Abyssal oceanic pressure crystalizes reef fossils for a +125% (2.25x) Fossil Dust multiplier.",
        unlockCostFD = 10000.0,
        growthMultiplier = 1.2,
        resourceMultiplier = 1.8,
        extinctionRiskMultiplier = 0.9,
        fossilDustMultiplier = 2.25,
        emoji = "🐟",
        biomeName = "Abyssal Reef",
        accentColorHex = 0xFF00E5FF
    ),
    TERRARIUM(
        id = "terrarium",
        displayName = "Botanical Terrarium",
        description = "Forest Biosphere. Lush petrified humus maximizes bio-fossilization with a +200% (3.0x) Fossil Dust multiplier!",
        unlockCostFD = 50000.0,
        growthMultiplier = 1.5,
        resourceMultiplier = 1.5,
        extinctionRiskMultiplier = 0.6,
        fossilDustMultiplier = 3.00,
        emoji = "🌿",
        biomeName = "Verdant Canopy",
        accentColorHex = 0xFF69F0AE
    );

    companion object {
        fun fromId(id: String): JarType = entries.find { it.id == id } ?: MASON
    }
}

enum class Era(
    val index: Int,
    val title: String,
    val subtitle: String,
    val requiredPopulation: Double,
    val basePopPerSec: Double,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val bossEventTitle: String,
    val bossEventDescription: String,
    val requiredFossilDust: Double = 0.0
) {
    PRIMORDIAL_SOUP(
        1, "Primordial Soup", "Single-celled amino acids forming in warm basement sediment",
        0.0, 1.0, 0xFF1B4D3E, 0xFF3DDC84,
        "The Spark of RNA", "Self-replicating molecules have spontaneously linked chains in the murky broth.",
        0.0
    ),
    FIRST_LIFE(
        2, "First Life", "Multicellular organisms gliding through miniature currents",
        1_000.0, 15.0, 0xFF0D5C75, 0xFF00E5FF,
        "Cellular Specialization", "Colonies of cells have organized into distinct symbiotic organisms.",
        10.0
    ),
    TRIBAL_AGE(
        3, "Tribal Age", "Tiny hunter-gatherer clusters gathering around microscopic hearths",
        50_000.0, 250.0, 0xFF8C4A15, 0xFFFFB74D,
        "The Sacred Spark", "Jar-dwellers have learned to ignite micro-embers from glass light refraction.",
        50.0
    ),
    AGRICULTURAL_AGE(
        4, "Agricultural Age", "Organized micro-farms carving terraces across the sediment floor",
        2_500_000.0, 4_500.0, 0xFF33691E, 0xFF8BC34A,
        "The Granary Revolution", "Surplus microscopic grain silos allow exponential population clustering.",
        200.0
    ),
    CITY_AGE(
        5, "City Age", "Miniature architectural monuments and bustling paved avenues",
        100_000_000.0, 95_000.0, 0xFF37474F, 0xFF90A4AE,
        "The Grand Forum", "Dozens of jar factions convene to draft the First Code of Glass Law.",
        1_000.0
    ),
    INDUSTRIAL_AGE(
        6, "Industrial Age", "Tiny steam stacks billow microscopic vapor against the glass walls",
        5_000_000_000.0, 2_400_000.0, 0xFF4E342E, 0xFFFF7043,
        "Steam & Iron", "Combustion engines rattle the jar's inner rim as production surges.",
        5_000.0
    ),
    DIGITAL_AGE(
        7, "Digital Age", "Blinking fiber optic matrices forming a radiant silicon network",
        250_000_000_000.0, 75_000_000.0, 0xFF003B73, 0xFF00E676,
        "The Global Jar-Net", "Every micro-citizen is interconnected in an instantaneous thought grid.",
        25_000.0
    ),
    SPACE_AGE(
        8, "Space Age", "Micro-satellites orbiting the jar rim and rockets arcing toward the lid",
        15_000_000_000_000.0, 2_800_000_000.0, 0xFF1A103C, 0xFFBB86FC,
        "Lid Discovery Expedition", "The first manned rocket touches the metallic lid: 'We found the sky ceiling!'",
        100_000.0
    ),
    ASCENSION(
        9, "Singularity & Ascension", "Transcendent beings dissolving into pure glowing cosmic energy",
        1_000_000_000_000_000.0, 120_000_000_000.0, 0xFF4A148C, 0xFFFFD700,
        "Transcending The Glass", "Consciousness vibrates at the resonance frequency of crystal glass.",
        500_000.0
    );

    companion object {
        fun fromIndex(index: Int): Era = entries.find { it.index == index } ?: PRIMORDIAL_SOUP
    }
}

enum class ExtinctionType(
    val displayName: String,
    val description: String,
    val minEra: Int,
    val bonusFossilDustMultiplier: Double,
    val iconName: String
) {
    NUCLEAR_WAR("Nuclear Holocaust", "Civilization tore itself apart in atomic fallout. The glass clouds with smoke.", 6, 1.2, "nuclear"),
    RESOURCE_COLLAPSE("Ecological Collapse", "Depletion of all organic broth caused catastrophic famine.", 4, 1.0, "desert"),
    PANDEMIC("Red Spore Contagion", "A virulent basement microbe swept through every settlement.", 3, 1.1, "virus"),
    METEOR_IMPACT("Countertop Cataclysm", "A stray basement marble struck the exterior jar glass, causing devastating shockwaves.", 1, 1.15, "meteor"),
    PEACEFUL_ASCENSION("Transcendent Rapture", "The civilization reached supreme enlightenment and ascended beyond physical form.", 9, 2.5, "ascension")
}

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
    @PrimaryKey val id: Int = 1,
    val totalFossilDust: Double = 0.0,
    val lifetimeFossilDust: Double = 0.0,
    val gems: Long = 0L,
    val selectedJarId: String = JarType.MASON.id,
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val shakeEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val lastActiveTimestamp: Long = System.currentTimeMillis(),
    val greatResetCount: Int = 0,
    val greatResetMultiplier: Double = 1.0,
    val speedUpActiveUntil: Long = 0L,
    val extinctionInsuranceCount: Int = 0,
    val selectedJarTheme: String = "default",
    val unlockedJarThemesJson: String = "[\"default\"]"
)

@Entity(tableName = "jar_states")
data class JarStateEntity(
    @PrimaryKey val jarId: String,
    val isUnlocked: Boolean = false,
    val currentEraIndex: Int = 1,
    val population: Double = 0.0,
    val organicMatter: Double = 10.0,
    val peakPopulation: Double = 0.0,
    val totalInGameYears: Double = 0.0,
    val upgradeLevelsJson: String = "{}",
    val natureMeter: Double = 0.0,
    val lastSavedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "fossil_upgrades")
data class FossilUpgradeEntity(
    @PrimaryKey val upgradeId: String,
    val level: Int = 0
)

@Entity(tableName = "civilization_history")
data class CivilizationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jarId: String,
    val jarName: String,
    val eraName: String,
    val peakPopulation: Double,
    val yearsSurvived: Double,
    val extinctionType: String,
    val fossilDustEarned: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "unlocked_achievements")
data class AchievementEntity(
    @PrimaryKey val achievementId: String,
    val unlockedTimestamp: Long = System.currentTimeMillis()
)

