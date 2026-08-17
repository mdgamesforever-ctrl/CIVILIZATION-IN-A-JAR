package com.example.data

enum class AutomatorType {
    AUTO_TAP,
    AUTO_CONVERT_OM,
    AUTO_ERA_ADVANCE
}

enum class FossilBranch(val displayName: String, val iconName: String) {
    GROWTH("Life Catalyst", "growth"),
    SURVIVAL("Resilience & Stability", "shield"),
    FOSSIL_DUST("Fossil Alchemy", "dust"),
    DIMENSIONS("Parallel Jars", "jar")
}

enum class FossilEffectType {
    STARTING_POP,
    GLOBAL_GROWTH_MULT,
    OM_TAP_POWER,
    PASSIVE_OM_GEN,
    EXTINCTION_RISK_REDUCTION,
    EARTHQUAKE_BONUS_RESIST,
    EVENT_REWARD_MULT,
    FOSSIL_DUST_GAIN_MULT,
    PASSIVE_FD_PER_MIN,
    ASCENSION_BONUS_FD,
    UNLOCK_SPICE_JAR,
    UNLOCK_JAM_JAR,
    UNLOCK_AQUARIUM,
    UNLOCK_TERRARIUM,
    MULTI_JAR_SYNERGY
}

data class PopulationUpgradeDef(
    val id: String,
    val eraIndex: Int,
    val title: String,
    val description: String,
    val baseCostPOP: Double,
    val baseCostOM: Double = 0.0,
    val costMultiplier: Double = 1.18,
    val popRateBonus: Double = 0.0,
    val omTapBonus: Double = 0.0,
    val isAutomator: Boolean = false,
    val automatorType: AutomatorType? = null,
    val iconEmoji: String = "✨"
)

data class FossilUpgradeDef(
    val id: String,
    val branch: FossilBranch,
    val title: String,
    val description: String,
    val maxLevel: Int,
    val baseCostFD: Double,
    val costMultiplier: Double = 1.6,
    val effectType: FossilEffectType,
    val effectValuePerLevel: Double,
    val iconEmoji: String = "💎"
)

object UpgradeCatalog {

    val POPULATION_UPGRADES: List<PopulationUpgradeDef> = listOf(
        // === ERA 1: PRIMORDIAL SOUP ===
        PopulationUpgradeDef(
            id = "era1_rna_replication",
            eraIndex = 1,
            title = "RNA Self-Replication",
            description = "Strands of early genetic code copy themselves spontaneously (+1 Pop/s)",
            baseCostPOP = 10.0,
            baseCostOM = 5.0,
            costMultiplier = 1.15,
            popRateBonus = 1.0,
            iconEmoji = "🧬"
        ),
        PopulationUpgradeDef(
            id = "era1_lipid_membrane",
            eraIndex = 1,
            title = "Lipid Micelles",
            description = "Fatty bubbles trap nutrients inside micro-vesicles (+3 Pop/s)",
            baseCostPOP = 45.0,
            costMultiplier = 1.16,
            popRateBonus = 3.0,
            iconEmoji = "🫧"
        ),
        PopulationUpgradeDef(
            id = "era1_hydrothermal_vent",
            eraIndex = 1,
            title = "Sub-Sediment Thermal Vent",
            description = "A warm current at the jar bottom fuels continuous life (+8 Pop/s)",
            baseCostPOP = 150.0,
            costMultiplier = 1.17,
            popRateBonus = 8.0,
            iconEmoji = "♨️"
        ),
        PopulationUpgradeDef(
            id = "era1_auto_absorber",
            eraIndex = 1,
            title = "Micro-Enzyme Automator",
            description = "Automator: Automatically absorbs 2 Organic Matter per second into living mass",
            baseCostPOP = 500.0,
            costMultiplier = 1.25,
            isAutomator = true,
            automatorType = AutomatorType.AUTO_CONVERT_OM,
            popRateBonus = 20.0,
            iconEmoji = "⚙️"
        ),

        // === ERA 2: FIRST LIFE ===
        PopulationUpgradeDef(
            id = "era2_flagella_propulsion",
            eraIndex = 2,
            title = "Flagella Propulsion",
            description = "Swimming tail organelles spread colonies across the water (+40 Pop/s)",
            baseCostPOP = 1_200.0,
            costMultiplier = 1.17,
            popRateBonus = 40.0,
            iconEmoji = "🦠"
        ),
        PopulationUpgradeDef(
            id = "era2_photosynthesis",
            eraIndex = 2,
            title = "Chloroplast Light-Trapping",
            description = "Harness sunlight passing through the glass wall (+120 Pop/s)",
            baseCostPOP = 4_500.0,
            costMultiplier = 1.18,
            popRateBonus = 120.0,
            iconEmoji = "☀️"
        ),
        PopulationUpgradeDef(
            id = "era2_cellular_colony",
            eraIndex = 2,
            title = "Symbiotic Biofilm",
            description = "Colonies coat the glass base in living mats (+350 Pop/s)",
            baseCostPOP = 15_000.0,
            costMultiplier = 1.19,
            popRateBonus = 350.0,
            iconEmoji = "🟢"
        ),
        PopulationUpgradeDef(
            id = "era2_auto_tapper",
            eraIndex = 2,
            title = "Ambient Tap Resonator",
            description = "Automator: Simulates 1 jar tap every second automatically",
            baseCostPOP = 35_000.0,
            costMultiplier = 1.30,
            isAutomator = true,
            automatorType = AutomatorType.AUTO_TAP,
            popRateBonus = 800.0,
            iconEmoji = "👆"
        ),

        // === ERA 3: TRIBAL AGE ===
        PopulationUpgradeDef(
            id = "era3_flint_fire",
            eraIndex = 3,
            title = "Glass Spark Hearth",
            description = "Mastering fire keeps tiny tribes warm through the basement chill (+2,000 Pop/s)",
            baseCostPOP = 75_000.0,
            costMultiplier = 1.18,
            popRateBonus = 2_000.0,
            iconEmoji = "🔥"
        ),
        PopulationUpgradeDef(
            id = "era3_sediment_dwellings",
            eraIndex = 3,
            title = "Sediment Huts",
            description = "Straw and clay clusters sprout across the bottom pebbles (+6,500 Pop/s)",
            baseCostPOP = 250_000.0,
            costMultiplier = 1.19,
            popRateBonus = 6_500.0,
            iconEmoji = "🛖"
        ),
        PopulationUpgradeDef(
            id = "era3_proto_language",
            eraIndex = 3,
            title = "Spoken Chants",
            description = "Vocal coordination unites distant micro-tribes (+18,000 Pop/s)",
            baseCostPOP = 800_000.0,
            costMultiplier = 1.20,
            popRateBonus = 18_000.0,
            iconEmoji = "🗣️"
        ),
        PopulationUpgradeDef(
            id = "era3_totem_altar",
            eraIndex = 3,
            title = "Altar of the Great Giant",
            description = "Cult of the giant hand tapping the jar inspires fanatic growth (+50,000 Pop/s)",
            baseCostPOP = 1_800_000.0,
            costMultiplier = 1.22,
            popRateBonus = 50_000.0,
            iconEmoji = "🗿"
        ),

        // === ERA 4: AGRICULTURAL AGE ===
        PopulationUpgradeDef(
            id = "era4_terraced_fields",
            eraIndex = 4,
            title = "Sediment Terraces",
            description = "Tilled soil on the jar slope yields bountiful micro-crops (+180,000 Pop/s)",
            baseCostPOP = 3_500_000.0,
            costMultiplier = 1.19,
            popRateBonus = 180_000.0,
            iconEmoji = "🌾"
        ),
        PopulationUpgradeDef(
            id = "era4_irrigation_canals",
            eraIndex = 4,
            title = "Condensation Aqueducts",
            description = "Diverts droplets running down the glass into water canals (+600,000 Pop/s)",
            baseCostPOP = 12_000_000.0,
            costMultiplier = 1.20,
            popRateBonus = 600_000.0,
            iconEmoji = "💧"
        ),
        PopulationUpgradeDef(
            id = "era4_selective_breeding",
            eraIndex = 4,
            title = "Micro-Livestock Domestication",
            description = "Tiny tardigrade mounts provide labor and food security (+2,000,000 Pop/s)",
            baseCostPOP = 40_000_000.0,
            costMultiplier = 1.21,
            popRateBonus = 2_000_000.0,
            iconEmoji = "🐂"
        ),
        PopulationUpgradeDef(
            id = "era4_bronze_tools",
            eraIndex = 4,
            title = "Bronze Smelting",
            description = "Metal tools quadruple sediment expansion efficiency (+6,500,000 Pop/s)",
            baseCostPOP = 80_000_000.0,
            costMultiplier = 1.22,
            popRateBonus = 6_500_000.0,
            iconEmoji = "⛏️"
        ),

        // === ERA 5: CITY AGE ===
        PopulationUpgradeDef(
            id = "era5_stone_citadels",
            eraIndex = 5,
            title = "Pebble Citadels",
            description = "Multi-story stone towers reach several millimeters tall (+25,000,000 Pop/s)",
            baseCostPOP = 150_000_000.0,
            costMultiplier = 1.20,
            popRateBonus = 25_000_000.0,
            iconEmoji = "🏰"
        ),
        PopulationUpgradeDef(
            id = "era5_paved_roads",
            eraIndex = 5,
            title = "Perimeter Highway Network",
            description = "Paved highways hug the inner curved circumference of the jar (+80,000,000 Pop/s)",
            baseCostPOP = 500_000_000.0,
            costMultiplier = 1.21,
            popRateBonus = 80_000_000.0,
            iconEmoji = "🛣️"
        ),
        PopulationUpgradeDef(
            id = "era5_written_constitution",
            eraIndex = 5,
            title = "Glass Constitution",
            description = "A codified rule of law unites millions of city dwellers (+280,000,000 Pop/s)",
            baseCostPOP = 1_800_000_000.0,
            costMultiplier = 1.22,
            popRateBonus = 280_000_000.0,
            iconEmoji = "📜"
        ),
        PopulationUpgradeDef(
            id = "era5_grand_academy",
            eraIndex = 5,
            title = "The Academy of the Countertop",
            description = "Philosophers debate what lies outside the Great Glass Boundary (+850,000,000 Pop/s)",
            baseCostPOP = 4_000_000_000.0,
            costMultiplier = 1.23,
            popRateBonus = 850_000_000.0,
            iconEmoji = "🏛️"
        ),

        // === ERA 6: INDUSTRIAL AGE ===
        PopulationUpgradeDef(
            id = "era6_steam_locomotive",
            eraIndex = 6,
            title = "Micro-Steam Locomotives",
            description = "Miniature freight trains loop around the jar sediment (+3,500,000,000 Pop/s)",
            baseCostPOP = 8_000_000_000.0,
            costMultiplier = 1.21,
            popRateBonus = 3_500_000_000.0,
            iconEmoji = "🚂"
        ),
        PopulationUpgradeDef(
            id = "era6_coal_factories",
            eraIndex = 6,
            title = "Soot Foundries",
            description = "Towering smokestacks condense black particles onto the glass (+12,000,000,000 Pop/s)",
            baseCostPOP = 30_000_000_000.0,
            costMultiplier = 1.22,
            popRateBonus = 12_000_000_000.0,
            iconEmoji = "🏭"
        ),
        PopulationUpgradeDef(
            id = "era6_electricity_grid",
            eraIndex = 6,
            title = "Arc-Lighting Circuit",
            description = "Copper filigree wires illuminate the entire jar night and day (+45,000,000,000 Pop/s)",
            baseCostPOP = 90_000_000_000.0,
            costMultiplier = 1.23,
            popRateBonus = 45_000_000_000.0,
            iconEmoji = "⚡"
        ),
        PopulationUpgradeDef(
            id = "era6_mass_production",
            eraIndex = 6,
            title = "Assembly Line Zenith",
            description = "Automated assembly plants produce consumer goods at breakneck pace (+150,000,000,000 Pop/s)",
            baseCostPOP = 200_000_000_000.0,
            costMultiplier = 1.24,
            popRateBonus = 150_000_000_000.0,
            iconEmoji = "📦"
        ),

        // === ERA 7: DIGITAL AGE ===
        PopulationUpgradeDef(
            id = "era7_silicon_microchip",
            eraIndex = 7,
            title = "Sub-Micron Microprocessors",
            description = "Supercomputers fit inside grains of sand (+600,000,000,000 Pop/s)",
            baseCostPOP = 350_000_000_000.0,
            costMultiplier = 1.22,
            popRateBonus = 600_000_000_000.0,
            iconEmoji = "💾"
        ),
        PopulationUpgradeDef(
            id = "era7_fiber_web",
            eraIndex = 7,
            title = "Fiber-Optic Matrix",
            description = "Glowing neon data pulses crisscross the jar landscape (+2,200,000,000,000 Pop/s)",
            baseCostPOP = 1_200_000_000_000.0,
            costMultiplier = 1.23,
            popRateBonus = 2_200_000_000_000.0,
            iconEmoji = "🌐"
        ),
        PopulationUpgradeDef(
            id = "era7_ai_superclusters",
            eraIndex = 7,
            title = "Jar-Mind Super Intelligence",
            description = "Artificial intelligence manages resource logistics across all districts (+8,000,000,000,000 Pop/s)",
            baseCostPOP = 4_500_000_000_000.0,
            costMultiplier = 1.24,
            popRateBonus = 8_000_000_000_000.0,
            iconEmoji = "🤖"
        ),
        PopulationUpgradeDef(
            id = "era7_auto_era",
            eraIndex = 7,
            title = "Evolution Auto-Compiler",
            description = "Automator: Instantly and automatically evolves to the next Era when ready",
            baseCostPOP = 12_000_000_000_000.0,
            costMultiplier = 1.30,
            isAutomator = true,
            automatorType = AutomatorType.AUTO_ERA_ADVANCE,
            popRateBonus = 25_000_000_000_000.0,
            iconEmoji = "🚀"
        ),

        // === ERA 8: SPACE AGE ===
        PopulationUpgradeDef(
            id = "era8_orbital_satellite",
            eraIndex = 8,
            title = "Jar-Rim Orbiting Satellites",
            description = "Micro-satellites sling around the jar's inner curvature (+80,000,000,000,000 Pop/s)",
            baseCostPOP = 20_000_000_000_000.0,
            costMultiplier = 1.23,
            popRateBonus = 80_000_000_000_000.0,
            iconEmoji = "🛰️"
        ),
        PopulationUpgradeDef(
            id = "era8_lid_space_station",
            eraIndex = 8,
            title = "Lid Underside Space Dock",
            description = "Colonies established on the metallic ceiling looking down at Earth (+300,000,000,000,000 Pop/s)",
            baseCostPOP = 80_000_000_000_000.0,
            costMultiplier = 1.24,
            popRateBonus = 300_000_000_000_000.0,
            iconEmoji = "🛸"
        ),
        PopulationUpgradeDef(
            id = "era8_antimatter_drives",
            eraIndex = 8,
            title = "Micro-Antimatter Drives",
            description = "Near-lightspeed propulsion inside the glass dome (+1,200,000,000,000,000 Pop/s)",
            baseCostPOP = 300_000_000_000_000.0,
            costMultiplier = 1.25,
            popRateBonus = 1_200_000_000_000_000.0,
            iconEmoji = "🌌"
        ),
        PopulationUpgradeDef(
            id = "era8_dyson_lid",
            eraIndex = 8,
            title = "Jar Cap Fusion Harvester",
            description = "Harvests external basement ceiling light at maximum efficiency (+4,500,000,000,000,000 Pop/s)",
            baseCostPOP = 800_000_000_000_000.0,
            costMultiplier = 1.26,
            popRateBonus = 4_500_000_000_000_000.0,
            iconEmoji = "⚛️"
        ),

        // === ERA 9: ASCENSION / SINGULARITY ===
        PopulationUpgradeDef(
            id = "era9_quantum_consciousness",
            eraIndex = 9,
            title = "Quantum Consciousness",
            description = "Physical bodies dissolve into pulsing coherent light fields (+20,000,000,000,000,000 Pop/s)",
            baseCostPOP = 1_500_000_000_000_000.0,
            costMultiplier = 1.25,
            popRateBonus = 20_000_000_000_000_000.0,
            iconEmoji = "✨"
        ),
        PopulationUpgradeDef(
            id = "era9_glass_resonance",
            eraIndex = 9,
            title = "Crystal Lattice Harmonizer",
            description = "Synchronizes the frequency of the jar's silica molecules (+80,000,000,000,000,000 Pop/s)",
            baseCostPOP = 6_000_000_000_000_000.0,
            costMultiplier = 1.26,
            popRateBonus = 80_000_000_000_000_000.0,
            iconEmoji = "🔮"
        ),
        PopulationUpgradeDef(
            id = "era9_dimensional_rift",
            eraIndex = 9,
            title = "Dimensional Sub-Rift",
            description = "Opens a microscopic wormhole to parallel kitchen realities (+350,000,000,000,000,000 Pop/s)",
            baseCostPOP = 25_000_000_000_000_000.0,
            costMultiplier = 1.28,
            popRateBonus = 350_000_000_000_000_000.0,
            iconEmoji = "🌀"
        ),
        PopulationUpgradeDef(
            id = "era9_omnipresent_essence",
            eraIndex = 9,
            title = "Omnipresent Jar Deity",
            description = "The civilization becomes one with the basement countertop (+1,500,000,000,000,000,000 Pop/s)",
            baseCostPOP = 100_000_000_000_000_000.0,
            costMultiplier = 1.30,
            popRateBonus = 1_500_000_000_000_000_000.0,
            iconEmoji = "👑"
        )
    )

    // === 20+ FOSSIL DUST PRESTIGE UPGRADES ===
    val FOSSIL_UPGRADES: List<FossilUpgradeDef> = listOf(
        // Branch 1: Life Catalyst (Growth)
        FossilUpgradeDef(
            id = "fd_primordial_infusion",
            branch = FossilBranch.GROWTH,
            title = "Primordial Infusion",
            description = "+50 starting population in new civilizations per level",
            maxLevel = 25,
            baseCostFD = 5.0,
            costMultiplier = 1.5,
            effectType = FossilEffectType.STARTING_POP,
            effectValuePerLevel = 50.0,
            iconEmoji = "🌱"
        ),
        FossilUpgradeDef(
            id = "fd_cellular_vigor",
            branch = FossilBranch.GROWTH,
            title = "Cellular Vigor",
            description = "+20% global population growth rate across all eras",
            maxLevel = 50,
            baseCostFD = 12.0,
            costMultiplier = 1.4,
            effectType = FossilEffectType.GLOBAL_GROWTH_MULT,
            effectValuePerLevel = 0.20,
            iconEmoji = "⚡"
        ),
        FossilUpgradeDef(
            id = "fd_organic_attractor",
            branch = FossilBranch.GROWTH,
            title = "Organic Attractor",
            description = "+100% Organic Matter gained per jar tap",
            maxLevel = 30,
            baseCostFD = 8.0,
            costMultiplier = 1.45,
            effectType = FossilEffectType.OM_TAP_POWER,
            effectValuePerLevel = 1.0,
            iconEmoji = "🧲"
        ),
        FossilUpgradeDef(
            id = "fd_ambient_photosynthesis",
            branch = FossilBranch.GROWTH,
            title = "Basement Radiance",
            description = "Generates +1.0 Organic Matter automatically per second per level",
            maxLevel = 20,
            baseCostFD = 25.0,
            costMultiplier = 1.6,
            effectType = FossilEffectType.PASSIVE_OM_GEN,
            effectValuePerLevel = 1.0,
            iconEmoji = "💡"
        ),
        FossilUpgradeDef(
            id = "fd_dna_memory",
            branch = FossilBranch.GROWTH,
            title = "Ancestral Memory",
            description = "+15% additional growth rate multiplier compounding per completed era",
            maxLevel = 20,
            baseCostFD = 75.0,
            costMultiplier = 1.7,
            effectType = FossilEffectType.GLOBAL_GROWTH_MULT,
            effectValuePerLevel = 0.15,
            iconEmoji = "🧬"
        ),

        // Branch 2: Resilience & Survival
        FossilUpgradeDef(
            id = "fd_reinforced_silica",
            branch = FossilBranch.SURVIVAL,
            title = "Reinforced Silica Glass",
            description = "-10% random extinction probability at Era 6+",
            maxLevel = 5,
            baseCostFD = 30.0,
            costMultiplier = 2.0,
            effectType = FossilEffectType.EXTINCTION_RISK_REDUCTION,
            effectValuePerLevel = 0.10,
            iconEmoji = "🛡️"
        ),
        FossilUpgradeDef(
            id = "fd_shock_absorbers",
            branch = FossilBranch.SURVIVAL,
            title = "Rubber Jar Coaster",
            description = "Earthquake shakes have +50% chance to trigger bonus OM instead of casualties",
            maxLevel = 10,
            baseCostFD = 40.0,
            costMultiplier = 1.6,
            effectType = FossilEffectType.EARTHQUAKE_BONUS_RESIST,
            effectValuePerLevel = 0.50,
            iconEmoji = "📳"
        ),
        FossilUpgradeDef(
            id = "fd_enlightened_diplomacy",
            branch = FossilBranch.SURVIVAL,
            title = "Grandma's Basement Archive",
            description = "+35% higher rewards from random narrative micro-events",
            maxLevel = 15,
            baseCostFD = 60.0,
            costMultiplier = 1.5,
            effectType = FossilEffectType.EVENT_REWARD_MULT,
            effectValuePerLevel = 0.35,
            iconEmoji = "📖"
        ),
        FossilUpgradeDef(
            id = "fd_adaptive_metabolism",
            branch = FossilBranch.SURVIVAL,
            title = "Adaptive Metabolism",
            description = "Civilization survives for 25% more in-game years before natural decay",
            maxLevel = 10,
            baseCostFD = 120.0,
            costMultiplier = 1.8,
            effectType = FossilEffectType.EXTINCTION_RISK_REDUCTION,
            effectValuePerLevel = 0.08,
            iconEmoji = "🫀"
        ),
        FossilUpgradeDef(
            id = "fd_golden_equilibrium",
            branch = FossilBranch.SURVIVAL,
            title = "Ecosystem Equilibrium",
            description = "-15% cost scaling penalty on high-tier population upgrades",
            maxLevel = 10,
            baseCostFD = 200.0,
            costMultiplier = 1.9,
            effectType = FossilEffectType.GLOBAL_GROWTH_MULT,
            effectValuePerLevel = 0.10,
            iconEmoji = "⚖️"
        ),

        // Branch 3: Fossil Alchemy
        FossilUpgradeDef(
            id = "fd_sediment_sieve",
            branch = FossilBranch.FOSSIL_DUST,
            title = "Sediment Sifter",
            description = "+30% Fossil Dust harvested upon any Extinction event",
            maxLevel = 30,
            baseCostFD = 20.0,
            costMultiplier = 1.45,
            effectType = FossilEffectType.FOSSIL_DUST_GAIN_MULT,
            effectValuePerLevel = 0.30,
            iconEmoji = "⏳"
        ),
        FossilUpgradeDef(
            id = "fd_amber_preservation",
            branch = FossilBranch.FOSSIL_DUST,
            title = "Amber Crystallization",
            description = "+50% Fossil Dust bonus when reaching Era 7 or higher",
            maxLevel = 20,
            baseCostFD = 100.0,
            costMultiplier = 1.6,
            effectType = FossilEffectType.FOSSIL_DUST_GAIN_MULT,
            effectValuePerLevel = 0.50,
            iconEmoji = "💎"
        ),
        FossilUpgradeDef(
            id = "fd_passive_distillery",
            branch = FossilBranch.FOSSIL_DUST,
            title = "Basement Dust Distillery",
            description = "Passively synthesizes +0.5 Fossil Dust per minute while idling",
            maxLevel = 20,
            baseCostFD = 150.0,
            costMultiplier = 1.7,
            effectType = FossilEffectType.PASSIVE_FD_PER_MIN,
            effectValuePerLevel = 0.5,
            iconEmoji = "🧪"
        ),
        FossilUpgradeDef(
            id = "fd_ascension_beacon",
            branch = FossilBranch.FOSSIL_DUST,
            title = "Transcendence Beacon",
            description = "+100% additional Fossil Dust reward for achieving Peaceful Ascension (Era 9)",
            maxLevel = 10,
            baseCostFD = 500.0,
            costMultiplier = 2.0,
            effectType = FossilEffectType.ASCENSION_BONUS_FD,
            effectValuePerLevel = 1.0,
            iconEmoji = "🌟"
        ),
        FossilUpgradeDef(
            id = "fd_fossil_catalyst_core",
            branch = FossilBranch.FOSSIL_DUST,
            title = "Paleo-Synthesis Matrix",
            description = "+25% Fossil Dust value from Peak Population milestones",
            maxLevel = 15,
            baseCostFD = 350.0,
            costMultiplier = 1.75,
            effectType = FossilEffectType.FOSSIL_DUST_GAIN_MULT,
            effectValuePerLevel = 0.25,
            iconEmoji = "🔮"
        ),

        // Branch 4: Dimensional Jars
        FossilUpgradeDef(
            id = "fd_unlock_spice_jar",
            branch = FossilBranch.DIMENSIONS,
            title = "Unlock: Cumin Spice Jar",
            description = "Discovers the Desert Civilization jar in the pantry cupboard",
            maxLevel = 1,
            baseCostFD = 250.0,
            costMultiplier = 1.0,
            effectType = FossilEffectType.UNLOCK_SPICE_JAR,
            effectValuePerLevel = 1.0,
            iconEmoji = "🧂"
        ),
        FossilUpgradeDef(
            id = "fd_unlock_jam_jar",
            branch = FossilBranch.DIMENSIONS,
            title = "Unlock: Strawberry Jam Jar",
            description = "Discovers the Swamp Civilization jar on the high shelf",
            maxLevel = 1,
            baseCostFD = 1_500.0,
            costMultiplier = 1.0,
            effectType = FossilEffectType.UNLOCK_JAM_JAR,
            effectValuePerLevel = 1.0,
            iconEmoji = "🍓"
        ),
        FossilUpgradeDef(
            id = "fd_unlock_aquarium",
            branch = FossilBranch.DIMENSIONS,
            title = "Unlock: Glass Aquatic Bowl",
            description = "Restores grandma's vintage aquatic ecosystem",
            maxLevel = 1,
            baseCostFD = 10_000.0,
            costMultiplier = 1.0,
            effectType = FossilEffectType.UNLOCK_AQUARIUM,
            effectValuePerLevel = 1.0,
            iconEmoji = "🐠"
        ),
        FossilUpgradeDef(
            id = "fd_unlock_terrarium",
            branch = FossilBranch.DIMENSIONS,
            title = "Unlock: Botanical Terrarium",
            description = "Uncovers the lush miniature rainforest under the basement window",
            maxLevel = 1,
            baseCostFD = 50_000.0,
            costMultiplier = 1.0,
            effectType = FossilEffectType.UNLOCK_TERRARIUM,
            effectValuePerLevel = 1.0,
            iconEmoji = "🌿"
        ),
        FossilUpgradeDef(
            id = "fd_countertop_resonance",
            branch = FossilBranch.DIMENSIONS,
            title = "Countertop Resonance",
            description = "Each unlocked jar boosts all other jars' growth speed by +15%",
            maxLevel = 10,
            baseCostFD = 1_000.0,
            costMultiplier = 1.8,
            effectType = FossilEffectType.MULTI_JAR_SYNERGY,
            effectValuePerLevel = 0.15,
            iconEmoji = "💫"
        )
    )
}
