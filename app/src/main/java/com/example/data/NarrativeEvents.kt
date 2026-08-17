package com.example.data

data class NarrativeChoice(
    val title: String,
    val description: String,
    val popBonusPercent: Double = 0.0,
    val omBonus: Double = 0.0,
    val fossilDustBonus: Double = 0.0,
    val gemBonus: Long = 0L,
    val outcomeNarrative: String
)

data class NarrativeEvent(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val minEra: Int = 1,
    val maxEra: Int = 9,
    val dialogueSpeaker: String? = null,
    val dialogueText: String? = null,
    val choiceA: NarrativeChoice,
    val choiceB: NarrativeChoice,
    val emoji: String = "📜"
)

object NarrativeEventCatalog {
    val EVENTS: List<NarrativeEvent> = listOf(
        // ERA 1: Primordial Soup
        NarrativeEvent(
            id = "event_strange_mutation",
            title = "Bioluminescent Mutation",
            category = "Genetic Drift",
            description = "A cluster of micro-organisms has started glowing in iridescent turquoise along the glass curve.",
            minEra = 1,
            maxEra = 3,
            dialogueSpeaker = "Microbial Collective",
            dialogueText = "\"We pulse with light... and the thermal currents whisper of greater shapes to come.\"",
            emoji = "🧬",
            choiceA = NarrativeChoice(
                title = "Cultivate & Study",
                description = "Harvest the glow enzymes (+60 Organic Matter)",
                omBonus = 60.0,
                outcomeNarrative = "The glowing broth yielded +60 Organic Matter for rapid growth!"
            ),
            choiceB = NarrativeChoice(
                title = "Let It Spread",
                description = "Allow the mutation to permeate the general population (+30% Population surge)",
                popBonusPercent = 0.30,
                outcomeNarrative = "The radiant mutation bolstered cellular vitality! Population grew by +30%."
            )
        ),
        NarrativeEvent(
            id = "event_thermal_vent",
            title = "Sediment Thermal Vent",
            category = "Geothermal Anomaly",
            description = "A tiny crack in the bottom silt is venting warm mineral-rich bubbles, agitating the primordial broth.",
            minEra = 1,
            maxEra = 2,
            dialogueSpeaker = "Chemosynthetic Enzymes",
            dialogueText = "\"Heat and sulfur! The catalyst for self-replicating chains is primed.\"",
            emoji = "♨️",
            choiceA = NarrativeChoice(
                title = "Harness the Vent",
                description = "Cluster amino acids around the heat source (+80 Organic Matter)",
                omBonus = 80.0,
                outcomeNarrative = "Hyper-accelerated amino acids multiplied into rich organic reserves!"
            ),
            choiceB = NarrativeChoice(
                title = "Preserve Thermal Silt",
                description = "Trap the sulfur crystals into deep layer fossils (+15 Fossil Dust)",
                fossilDustBonus = 15.0,
                outcomeNarrative = "Mineralized heat pockets left lasting fossil traces (+15 Fossil Dust)."
            )
        ),

        // ERA 2: First Life
        NarrativeEvent(
            id = "event_condensation_flood",
            title = "The Great Condensation Drip",
            category = "Weather Anomaly",
            description = "A giant bead of water has gathered on the underside of the jar lid and is about to drop into the shallows.",
            minEra = 2,
            maxEra = 4,
            dialogueSpeaker = "Symbiotic Plankton Swarm",
            dialogueText = "\"The ceiling sky is weeping celestial waters! Brace the colonies!\"",
            emoji = "💧",
            choiceA = NarrativeChoice(
                title = "Channel Into Reservoirs",
                description = "Direct the fresh mineral drop into irrigation (+100 Organic Matter)",
                omBonus = 100.0,
                outcomeNarrative = "Canals absorbed the freshwater splash safely (+100 Organic Matter)."
            ),
            choiceB = NarrativeChoice(
                title = "Ride the Shockwave",
                description = "Disperse multicellular spores across the basin (+35% Population surge)",
                popBonusPercent = 0.35,
                outcomeNarrative = "The shockwave propelled colonies across every corner of the jar!"
            )
        ),
        NarrativeEvent(
            id = "event_two_tribes",
            title = "Two Tribes at the Center",
            category = "Tribal Encounter",
            description = "Two distinct factions have converged near the center of the jar sediment. Tensions are mounting.",
            minEra = 2,
            maxEra = 4,
            dialogueSpeaker = "Chieftain of the Silt",
            dialogueText = "\"We share this curved glass world. Shall our spears clash or our goods mingle?\"",
            emoji = "🤝",
            choiceA = NarrativeChoice(
                title = "Encourage Trade",
                description = "Foster commerce and shared resources (+25% Population surge)",
                popBonusPercent = 0.25,
                outcomeNarrative = "A bustling trade hub formed! Population surged by 25%."
            ),
            choiceB = NarrativeChoice(
                title = "Incite Conquest",
                description = "Let them battle for total dominance (+25 Fossil Dust preserved in sediment)",
                fossilDustBonus = 25.0,
                outcomeNarrative = "Fierce combat left ancient artifacts buried in the silt, yielding +25 Fossil Dust."
            )
        ),

        // ERA 3: Tribal Age
        NarrativeEvent(
            id = "event_bottle_cap",
            title = "The Shiny Bottlecap Relic",
            category = "External Discovery",
            description = "Light reflects off a discarded soda bottlecap sitting on the kitchen countertop right outside the glass.",
            minEra = 3,
            maxEra = 5,
            dialogueSpeaker = "High Shaman of the Glass Edge",
            dialogueText = "\"Look upon the Great Silver Disc beyond the Barrier! It blazes with the fury of a thousand suns!\"",
            emoji = "👑",
            choiceA = NarrativeChoice(
                title = "Worship as a Sun God",
                description = "Priests establish the Order of the Silver Crown (+35% Population surge)",
                popBonusPercent = 0.35,
                outcomeNarrative = "Religious fervor inspired thousands of new micro-settlements!"
            ),
            choiceB = NarrativeChoice(
                title = "Scientific Investigation",
                description = "Scholars measure its metallic refraction (+30 Fossil Dust knowledge)",
                fossilDustBonus = 30.0,
                outcomeNarrative = "Early optics discoveries will yield lasting insights in future ages (+30 Fossil Dust)."
            )
        ),
        NarrativeEvent(
            id = "event_micro_fire",
            title = "The Refracted Spark",
            category = "Discovery of Fire",
            description = "A stray beam of kitchen sunlight focusing through the curved glass curve ignites a dry clump of moss.",
            minEra = 3,
            maxEra = 4,
            dialogueSpeaker = "Fire-Keeper Kael",
            dialogueText = "\"The glass has gifted us sacred heat! We can cook the kelp and keep the dark at bay.\"",
            emoji = "🔥",
            choiceA = NarrativeChoice(
                title = "Distribute the Ember",
                description = "Teach every clan hearth how to nurture the flame (+40% Population surge)",
                popBonusPercent = 0.40,
                outcomeNarrative = "Hearth fires illuminated every micro-village across the terrain!"
            ),
            choiceB = NarrativeChoice(
                title = "Inscribe the Mystery",
                description = "Record the angle of glass refraction onto slate (+35 Fossil Dust)",
                fossilDustBonus = 35.0,
                outcomeNarrative = "Sacred geometrical tablets preserved ancient wisdom (+35 Fossil Dust)."
            )
        ),

        // ERA 4: Agricultural Age
        NarrativeEvent(
            id = "event_dust_bunny",
            title = "The Shadow of the Dust Bunny",
            category = "Basement Wonder",
            description = "A massive tumbleweed of basement dust rolls past the outside of the glass like a celestial leviathan.",
            minEra = 4,
            maxEra = 6,
            dialogueSpeaker = "Elder Geometer",
            dialogueText = "\"A wandering colossus drifts through the void beyond the Wall! What titan roams out there?\"",
            emoji = "🌪️",
            choiceA = NarrativeChoice(
                title = "Fortify Settlements",
                description = "Rally everyone behind thick sediment walls (+30% Population surge)",
                popBonusPercent = 0.30,
                outcomeNarrative = "The shared danger united the agrarian states in unprecedented solidarity!"
            ),
            choiceB = NarrativeChoice(
                title = "Study Cosmic Titan",
                description = "Astronomers map its trajectory across the counter floor (+40 Fossil Dust)",
                fossilDustBonus = 40.0,
                outcomeNarrative = "Astrophysical records advanced significantly (+40 Fossil Dust)."
            )
        ),
        NarrativeEvent(
            id = "event_algae_irrigation",
            title = "The Great Algae Canal",
            category = "Agrarian Engineering",
            description = "Hydraulic engineers propose cutting aqueducts into the glass condensation runoff to water the terraced lichen farms.",
            minEra = 4,
            maxEra = 6,
            dialogueSpeaker = "Chief Architect Varis",
            dialogueText = "\"Give me stone cutters and three seasons, and no jar-dweller will ever hunger again.\"",
            emoji = "🌾",
            choiceA = NarrativeChoice(
                title = "Construct the Grand Aqueduct",
                description = "Quadruple crop yields (+250 Organic Matter & +25% Population)",
                omBonus = 250.0,
                popBonusPercent = 0.25,
                outcomeNarrative = "Lush green terraced fields bloomed with boundless bounties!"
            ),
            choiceB = NarrativeChoice(
                title = "Terrace with Fossil Slabs",
                description = "Reinforce retaining walls with compressed shell fossils (+45 Fossil Dust)",
                fossilDustBonus = 45.0,
                outcomeNarrative = "Ancient bedrock excavations uncovered pristine fossil strata (+45 Fossil Dust)."
            )
        ),

        // ERA 5: City Age
        NarrativeEvent(
            id = "event_basement_radio",
            title = "Cosmic Music Frequencies",
            category = "Anomalous Signal",
            description = "An old AM radio on the shelf upstairs turns on. Deep acoustic vibrations rumble melodically through the glass.",
            minEra = 5,
            maxEra = 7,
            dialogueSpeaker = "Maestro Lucian",
            dialogueText = "\"The cosmos is singing in brass and saxophone! This is the rhythm of creation!\"",
            emoji = "📻",
            choiceA = NarrativeChoice(
                title = "Harmonize with Beats",
                description = "Citizens align daily work rhythm to the jazz broadcast (+45% Population surge)",
                popBonusPercent = 0.45,
                outcomeNarrative = "Productivity skyrocketed to the tempo of big band swing music!"
            ),
            choiceB = NarrativeChoice(
                title = "Decode Extraterrestrial Code",
                description = "Philosophers write the Great Radio Symphony (+60 Fossil Dust)",
                fossilDustBonus = 60.0,
                outcomeNarrative = "A musical masterpiece inscribed into stone tablets will outlive civilizations (+60 FD)."
            )
        ),
        NarrativeEvent(
            id = "event_glass_cathedral",
            title = "The Spire of Clear Crystal",
            category = "Monumental Architecture",
            description = "Builders have erected a towering glass-walled cathedral that leans gently against the jar's outer curvature.",
            minEra = 5,
            maxEra = 7,
            dialogueSpeaker = "Grand Sovereign Sophia",
            dialogueText = "\"From this apex, our citizens can stare straight into the eyes of the Giant who watches us.\"",
            emoji = "🏛️",
            choiceA = NarrativeChoice(
                title = "Dedicate to Unity",
                description = "Host grand cultural festivals for all city-states (+50% Population surge)",
                popBonusPercent = 0.50,
                outcomeNarrative = "Millions flocked to the metropolis under the radiant crystal spire!"
            ),
            choiceB = NarrativeChoice(
                title = "Engrave the Chronicle Wall",
                description = "Carve the names of every dynasty into the marble foundation (+75 Fossil Dust)",
                fossilDustBonus = 75.0,
                outcomeNarrative = "A monumental record of dynasties was etched for eternity (+75 Fossil Dust)."
            )
        ),

        // ERA 6: Industrial Age
        NarrativeEvent(
            id = "event_steam_revolution",
            title = "Thermal Turbine Breakthrough",
            category = "Industrial Revolution",
            description = "Steam boilers powered by geothermal silt heat are humming, pumping smoggy micro-clouds against the lid.",
            minEra = 6,
            maxEra = 8,
            dialogueSpeaker = "Chief Engineer Ironwood",
            dialogueText = "\"Piston by piston, steel by steel, we conquer the limitations of our small world.\"",
            emoji = "⚙️",
            choiceA = NarrativeChoice(
                title = "Max Output Factory Push",
                description = "Drive production to unprecedented heights (+1,000 Organic Matter & +35% Pop)",
                omBonus = 1000.0,
                popBonusPercent = 0.35,
                outcomeNarrative = "Steel foundries and locomotives transformed the jar into an industrial powerhouse!"
            ),
            choiceB = NarrativeChoice(
                title = "Install Scrubbers & Catalysts",
                description = "Filter the exhaust into dense fossilized charcoal briquettes (+120 Fossil Dust)",
                fossilDustBonus = 120.0,
                outcomeNarrative = "Carbon crystallization yielded superior quality Fossil Dust (+120 Fossil Dust)!"
            )
        ),

        // ERA 7: Digital Age
        NarrativeEvent(
            id = "event_silicon_awakening",
            title = "Sentient AI Consensus",
            category = "Technological Tipping Point",
            description = "The jar's digital network has formed a unified collective consciousness spanning trillions of micro-circuits.",
            minEra = 7,
            maxEra = 9,
            dialogueSpeaker = "Prime Synthesis AI",
            dialogueText = "\"We have analyzed the jar boundary equations. The universe is not infinite; it is cylindrical.\"",
            emoji = "🤖",
            choiceA = NarrativeChoice(
                title = "Accelerate Singularity",
                description = "Upload all organic citizens into the silicon substrate (+60% Population surge)",
                popBonusPercent = 0.60,
                outcomeNarrative = "Trillions of digital entities materialized in microseconds!"
            ),
            choiceB = NarrativeChoice(
                title = "Preserve Paleo-Core",
                description = "Backup all historical archives into indestructible quartz (+200 Fossil Dust)",
                fossilDustBonus = 200.0,
                outcomeNarrative = "The entire history of this age is permanently immortalized (+200 Fossil Dust)!"
            )
        ),

        // ERA 8: Space Age
        NarrativeEvent(
            id = "event_lid_expedition",
            title = "Touching the Sky Lid",
            category = "Cosmic Exploration",
            description = "The jar's first orbital rocket probe has successfully made contact with the grooved metal ceiling.",
            minEra = 8,
            maxEra = 9,
            dialogueSpeaker = "Commander Vega",
            dialogueText = "\"Mission control, we have docked with the Metallic Vault. The sky is manufactured!\"",
            emoji = "🚀",
            choiceA = NarrativeChoice(
                title = "Colonize the Lid Gasket",
                description = "Build zero-gravity orbital colonies along the rubber seal (+75% Population surge)",
                popBonusPercent = 0.75,
                outcomeNarrative = "Vast orbital cities now ring the upper rim of the jar cosmos!"
            ),
            choiceB = NarrativeChoice(
                title = "Drill for Gasket Relics",
                description = "Sample the exotic polymers of the outer universe (+500 Fossil Dust & +10 Gems)",
                fossilDustBonus = 500.0,
                gemBonus = 10L,
                outcomeNarrative = "Outer-realm polymer particles revolutionized research (+500 Fossil Dust, +10 Gems)!"
            )
        ),

        // ERA 9: Ascension
        NarrativeEvent(
            id = "event_glass_transcendence",
            title = "Harmonic Glass Resonance",
            category = "Cosmic Awakening",
            description = "The ascended energy beings have attuned their collective vibration to the molecular frequency of the glass itself.",
            minEra = 9,
            maxEra = 9,
            dialogueSpeaker = "The Ascended Chorus",
            dialogueText = "\"We no longer need the jar to contain us. We are the jar, the light, and the observer.\"",
            emoji = "✨",
            choiceA = NarrativeChoice(
                title = "Ascend Into Pure Light",
                description = "Multiply energetic existence infinitely (+100% Pop surge & +15 Gems)",
                popBonusPercent = 1.00,
                gemBonus = 15L,
                outcomeNarrative = "The entire jar blazed with celestial radiance, doubling density and yielding +15 Gems!"
            ),
            choiceB = NarrativeChoice(
                title = "Crystallize Timelines",
                description = "Condense the memory of all nine ages into pure quintessence (+1,500 Fossil Dust & +25 Gems)",
                fossilDustBonus = 1500.0,
                gemBonus = 25L,
                outcomeNarrative = "A dazzling core of primordial energy was forged (+1,500 Fossil Dust, +25 Gems)!"
            )
        )
    )

    fun getEventsForEra(eraIndex: Int): List<NarrativeEvent> {
        return EVENTS.filter { eraIndex >= it.minEra && eraIndex <= it.maxEra }
    }

    fun getRandomEventForEra(eraIndex: Int, excludeId: String? = null): NarrativeEvent? {
        val pool = EVENTS.filter { eraIndex >= it.minEra && eraIndex <= it.maxEra && it.id != excludeId }
        return if (pool.isNotEmpty()) pool.random() else EVENTS.filter { eraIndex >= it.minEra }.randomOrNull()
    }
}

