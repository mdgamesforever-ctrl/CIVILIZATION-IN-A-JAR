package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

data class OfflineEarningsResult(
    val elapsedSeconds: Long,
    val jarEarnings: Map<String, Pair<Double, Double>>, // JarId -> (PopGained, OMGained)
    val fossilDustGained: Double = 0.0,
    val dustRatePerMinute: Double = 0.0,
    val gemsGained: Long = 0L
)

class GameRepository(private val dao: GameDao) {

    fun getPlayerProfile(): Flow<PlayerProfileEntity?> = dao.getPlayerProfile()
    suspend fun getPlayerProfileDirect(): PlayerProfileEntity? = dao.getPlayerProfileDirect()
    fun getAllJarStates(): Flow<List<JarStateEntity>> = dao.getAllJarStates()
    suspend fun getAllJarStatesDirect(): List<JarStateEntity> = dao.getAllJarStatesDirect()
    fun getJarState(jarId: String): Flow<JarStateEntity?> = dao.getJarState(jarId)
    fun getAllFossilUpgrades(): Flow<List<FossilUpgradeEntity>> = dao.getAllFossilUpgrades()
    fun getCivilizationHistory(): Flow<List<CivilizationHistoryEntity>> = dao.getCivilizationHistory()
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>> = dao.getUnlockedAchievements()
    suspend fun getUnlockedAchievementsDirect(): List<AchievementEntity> = dao.getUnlockedAchievementsDirect()

    suspend fun unlockAchievement(achievementId: String, rewardFD: Double = 0.0) = withContext(Dispatchers.IO) {
        val existing = dao.getUnlockedAchievementsDirect()
        if (existing.any { it.achievementId == achievementId }) return@withContext false
        dao.insertAchievement(AchievementEntity(achievementId = achievementId))
        if (rewardFD > 0.0) {
            val profile = dao.getPlayerProfileDirect()
            if (profile != null) {
                dao.insertOrUpdateProfile(
                    profile.copy(
                        totalFossilDust = profile.totalFossilDust + rewardFD,
                        lifetimeFossilDust = profile.lifetimeFossilDust + rewardFD
                    )
                )
            }
        }
        return@withContext true
    }

    suspend fun initializeDefaultsIfNeeded(): OfflineEarningsResult? = withContext(Dispatchers.IO) {
        var profile = dao.getPlayerProfileDirect()
        val now = System.currentTimeMillis()

        if (profile == null) {
            profile = PlayerProfileEntity(
                id = 1,
                totalFossilDust = 0.0,
                lifetimeFossilDust = 0.0,
                selectedJarId = JarType.MASON.id,
                soundEnabled = true,
                musicEnabled = true,
                shakeEnabled = true,
                notificationsEnabled = true,
                lastActiveTimestamp = now
            )
            dao.insertOrUpdateProfile(profile)

            // Initialize all 5 jar types
            val initialJars = listOf(
                JarStateEntity(
                    jarId = JarType.MASON.id,
                    isUnlocked = true,
                    currentEraIndex = 1,
                    population = 0.0,
                    organicMatter = 15.0,
                    peakPopulation = 0.0,
                    totalInGameYears = 0.0,
                    upgradeLevelsJson = "{}",
                    natureMeter = 0.0,
                    lastSavedTimestamp = now
                ),
                JarStateEntity(jarId = JarType.SPICE.id, isUnlocked = false),
                JarStateEntity(jarId = JarType.JAM.id, isUnlocked = false),
                JarStateEntity(jarId = JarType.AQUARIUM.id, isUnlocked = false),
                JarStateEntity(jarId = JarType.TERRARIUM.id, isUnlocked = false)
            )
            dao.insertJarStates(initialJars)
            return@withContext null
        }

        return@withContext calculateOfflineProductionInternal(now, profile)
    }

    suspend fun calculateOfflineProduction(now: Long = System.currentTimeMillis()): OfflineEarningsResult? = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext null
        return@withContext calculateOfflineProductionInternal(now, profile)
    }

    private suspend fun calculateOfflineProductionInternal(now: Long, profile: PlayerProfileEntity): OfflineEarningsResult? {
        // Calculate offline earnings if returning player
        val elapsedMs = now - profile.lastActiveTimestamp
        val elapsedSec = (elapsedMs / 1000).coerceAtLeast(0L)

        // Only give offline earnings if away for at least 15 seconds
        if (elapsedSec < 15L) {
            dao.insertOrUpdateProfile(profile.copy(lastActiveTimestamp = now))
            return null
        }

        // Cap offline earnings at 24 hours (86400 seconds)
        val cappedElapsedSec = min(elapsedSec, 86400L)
        val fossilUpgrades = dao.getAllFossilUpgradesDirect().associate { it.upgradeId to it.level }
        val globalGrowthMult = calculateGlobalGrowthMultiplier(fossilUpgrades)
        val passiveOmGen = calculatePassiveOmPerSec(fossilUpgrades)

        val jarStates = mutableListOf<JarStateEntity>()
        val earningsMap = mutableMapOf<String, Pair<Double, Double>>()

        val allJars = dao.getAllJarStatesDirect()
        val unlockedCount = allJars.count { it.isUnlocked }.coerceAtLeast(1)

        val speedUpMult = if (profile.speedUpActiveUntil > now) 2.0 else 1.0
        val unlockedJarTypes = allJars.filter { it.isUnlocked }.map { JarType.fromId(it.jarId) }.ifEmpty { listOf(JarType.MASON) }
        val activeJarType = JarType.fromId(profile.selectedJarId)

        val passiveFdRatePerSec = calculatePassiveFossilDustRatePerSec(
            fossilUpgrades = fossilUpgrades,
            unlockedJars = unlockedJarTypes,
            activeJarType = activeJarType,
            greatResetMultiplier = profile.greatResetMultiplier
        ) * speedUpMult

        val fdGainedRaw = passiveFdRatePerSec * cappedElapsedSec
        val fossilDustGained = if (fdGainedRaw >= 0.01) {
            kotlin.math.round(fdGainedRaw * 100.0) / 100.0
        } else 0.0

        for (jarType in JarType.entries) {
            val jar = allJars.find { it.jarId == jarType.id } ?: continue
            if (!jar.isUnlocked) continue

            val upgLevels = parseUpgradeLevels(jar.upgradeLevelsJson)
            val baseRate = calculateJarPopRate(jar.currentEraIndex, upgLevels, jarType)
            val effectiveRate = baseRate * globalGrowthMult * jarType.growthMultiplier * speedUpMult

            val popGained = effectiveRate * cappedElapsedSec
            val omGained = (passiveOmGen * cappedElapsedSec * jarType.resourceMultiplier)

            val newPop = jar.population + popGained
            val newOM = jar.organicMatter + omGained
            val newPeak = max(jar.peakPopulation, newPop)
            val newYears = jar.totalInGameYears + (cappedElapsedSec * 0.1)

            val updatedJar = jar.copy(
                population = newPop,
                organicMatter = newOM,
                peakPopulation = newPeak,
                totalInGameYears = newYears,
                lastSavedTimestamp = now
            )
            jarStates.add(updatedJar)
            earningsMap[jarType.id] = Pair(popGained, omGained)
        }

        dao.insertJarStates(jarStates)
        dao.insertOrUpdateProfile(
            profile.copy(
                totalFossilDust = profile.totalFossilDust + fossilDustGained,
                lifetimeFossilDust = profile.lifetimeFossilDust + fossilDustGained,
                lastActiveTimestamp = now
            )
        )

        return OfflineEarningsResult(
            elapsedSeconds = cappedElapsedSec,
            jarEarnings = earningsMap,
            fossilDustGained = fossilDustGained,
            dustRatePerMinute = passiveFdRatePerSec * 60.0
        )
    }

    suspend fun saveGameTick(
        jarId: String,
        population: Double,
        organicMatter: Double,
        peakPopulation: Double,
        inGameYears: Double,
        eraIndex: Int,
        natureMeter: Double,
        upgradeLevelsJson: String
    ) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val jar = dao.getJarStateDirect(jarId) ?: return@withContext
        val updated = jar.copy(
            population = population,
            organicMatter = organicMatter,
            peakPopulation = max(peakPopulation, population),
            totalInGameYears = inGameYears,
            currentEraIndex = eraIndex,
            natureMeter = natureMeter,
            upgradeLevelsJson = upgradeLevelsJson,
            lastSavedTimestamp = now
        )
        dao.insertOrUpdateJar(updated)

        val profile = dao.getPlayerProfileDirect()
        if (profile != null) {
            dao.insertOrUpdateProfile(profile.copy(lastActiveTimestamp = now))
        }
    }

    suspend fun selectJar(jarId: String) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        dao.insertOrUpdateProfile(profile.copy(selectedJarId = jarId))
    }

    suspend fun unlockJar(jarType: JarType, costFD: Double) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        if (profile.totalFossilDust < costFD) return@withContext

        val updatedProfile = profile.copy(totalFossilDust = profile.totalFossilDust - costFD)
        dao.insertOrUpdateProfile(updatedProfile)

        val jar = dao.getJarStateDirect(jarType.id) ?: JarStateEntity(jarId = jarType.id)
        dao.insertOrUpdateJar(
            jar.copy(
                isUnlocked = true,
                currentEraIndex = 1,
                population = 0.0,
                organicMatter = 20.0,
                lastSavedTimestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun buyFossilUpgrade(upgradeDef: FossilUpgradeDef, currentLevel: Int) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val cost = upgradeDef.baseCostFD * (upgradeDef.costMultiplier.pow(currentLevel.toDouble()))
        if (profile.totalFossilDust < cost) return@withContext

        dao.insertOrUpdateProfile(profile.copy(totalFossilDust = profile.totalFossilDust - cost))
        dao.insertFossilUpgrade(FossilUpgradeEntity(upgradeDef.id, currentLevel + 1))

        // Check if upgrade unlocks a jar type
        when (upgradeDef.effectType) {
            FossilEffectType.UNLOCK_SPICE_JAR -> unlockJarStateDirect(JarType.SPICE.id)
            FossilEffectType.UNLOCK_JAM_JAR -> unlockJarStateDirect(JarType.JAM.id)
            FossilEffectType.UNLOCK_AQUARIUM -> unlockJarStateDirect(JarType.AQUARIUM.id)
            FossilEffectType.UNLOCK_TERRARIUM -> unlockJarStateDirect(JarType.TERRARIUM.id)
            else -> {}
        }
    }

    private suspend fun unlockJarStateDirect(jarId: String) {
        val jar = dao.getJarStateDirect(jarId) ?: JarStateEntity(jarId = jarId)
        if (!jar.isUnlocked) {
            dao.insertOrUpdateJar(jar.copy(isUnlocked = true, currentEraIndex = 1, population = 0.0, organicMatter = 25.0))
        }
    }

    suspend fun triggerExtinction(
        jarId: String,
        extinctionType: ExtinctionType,
        customFossilDustEarned: Double,
        greatResetMultiplierGain: Double = 0.0
    ): Double = withContext(Dispatchers.IO) {
        val jar = dao.getJarStateDirect(jarId) ?: return@withContext 0.0
        val profile = dao.getPlayerProfileDirect() ?: return@withContext 0.0
        val jarType = JarType.fromId(jarId)
        val era = Era.fromIndex(jar.currentEraIndex)

        // Save history entry
        val historyEntry = CivilizationHistoryEntity(
            jarId = jarId,
            jarName = jarType.displayName,
            eraName = era.title,
            peakPopulation = jar.peakPopulation,
            yearsSurvived = jar.totalInGameYears,
            extinctionType = extinctionType.displayName,
            fossilDustEarned = customFossilDustEarned,
            timestamp = System.currentTimeMillis()
        )
        dao.insertHistoryEntry(historyEntry)

        val newMultiplier = (profile.greatResetMultiplier * (1.0 + greatResetMultiplierGain)).coerceAtLeast(1.0)

        // Award Fossil Dust and update Great Reset multiplier to player profile
        val updatedProfile = profile.copy(
            totalFossilDust = profile.totalFossilDust + customFossilDustEarned,
            lifetimeFossilDust = profile.lifetimeFossilDust + customFossilDustEarned,
            greatResetCount = profile.greatResetCount + 1,
            greatResetMultiplier = newMultiplier
        )
        dao.insertOrUpdateProfile(updatedProfile)

        // Reset jar to Era 1 with starting bonuses
        val fossilUpgrades = dao.getAllFossilUpgradesDirect().associate { it.upgradeId to it.level }
        val startingPop = calculateStartingPopulation(fossilUpgrades)

        val resetJar = jar.copy(
            currentEraIndex = 1,
            population = startingPop,
            organicMatter = 20.0,
            peakPopulation = startingPop,
            totalInGameYears = 0.0,
            upgradeLevelsJson = "{}",
            natureMeter = 0.0,
            lastSavedTimestamp = System.currentTimeMillis()
        )
        dao.insertOrUpdateJar(resetJar)

        return@withContext customFossilDustEarned
    }

    /**
     * Atomically adds gems to the persistent player profile.
     * Single dedicated mutation method for adding gems.
     */
    suspend fun addGems(amount: Long) = withContext(Dispatchers.IO) {
        if (amount <= 0) return@withContext
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val updated = profile.copy(gems = profile.gems + amount)
        dao.insertOrUpdateProfile(updated)
    }

    /**
     * Atomically spends gems from the player profile if balance is sufficient.
     * Guaranteed never to drop below 0. Returns true if successful, false otherwise.
     */
    suspend fun spendGems(amount: Long): Boolean = withContext(Dispatchers.IO) {
        if (amount <= 0) return@withContext false
        val profile = dao.getPlayerProfileDirect() ?: return@withContext false
        if (profile.gems < amount) {
            return@withContext false
        }
        val updated = profile.copy(gems = profile.gems - amount)
        dao.insertOrUpdateProfile(updated)
        return@withContext true
    }

    /**
     * Activates or extends the 2x population speed-up booster.
     */
    suspend fun applySpeedUp(durationMs: Long = 30 * 60 * 1000L) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val now = System.currentTimeMillis()
        val currentExpiry = profile.speedUpActiveUntil
        val newExpiry = if (currentExpiry > now) {
            currentExpiry + durationMs
        } else {
            now + durationMs
        }
        dao.insertOrUpdateProfile(profile.copy(speedUpActiveUntil = newExpiry))
    }

    /**
     * Adds extinction insurance protection tokens.
     */
    suspend fun addExtinctionInsurance(count: Int = 1) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val updated = profile.copy(extinctionInsuranceCount = profile.extinctionInsuranceCount + count)
        dao.insertOrUpdateProfile(updated)
    }

    /**
     * Consumes 1 extinction insurance token if available to shield against a cataclysm.
     * Returns true if insurance was consumed and protected the jar, false otherwise.
     */
    suspend fun consumeExtinctionInsurance(): Boolean = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext false
        if (profile.extinctionInsuranceCount > 0) {
            val updated = profile.copy(extinctionInsuranceCount = profile.extinctionInsuranceCount - 1)
            dao.insertOrUpdateProfile(updated)
            return@withContext true
        }
        return@withContext false
    }

    /**
     * Unlocks and equips a cosmetic jar theme.
     */
    suspend fun unlockAndSelectJarTheme(themeId: String) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val unlockedSet = StoreDefinitions.parseUnlockedThemes(profile.unlockedJarThemesJson).toMutableSet()
        unlockedSet.add(themeId)
        val updatedJson = StoreDefinitions.serializeUnlockedThemes(unlockedSet)
        dao.insertOrUpdateProfile(
            profile.copy(
                selectedJarTheme = themeId,
                unlockedJarThemesJson = updatedJson
            )
        )
    }

    /**
     * Selects an already unlocked jar theme.
     */
    suspend fun selectJarTheme(themeId: String) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        val unlockedSet = StoreDefinitions.parseUnlockedThemes(profile.unlockedJarThemesJson)
        if (unlockedSet.contains(themeId)) {
            dao.insertOrUpdateProfile(profile.copy(selectedJarTheme = themeId))
        }
    }

    suspend fun updateSettings(
        sound: Boolean,
        music: Boolean,
        shake: Boolean,
        notifications: Boolean
    ) = withContext(Dispatchers.IO) {
        val profile = dao.getPlayerProfileDirect() ?: return@withContext
        dao.insertOrUpdateProfile(
            profile.copy(
                soundEnabled = sound,
                musicEnabled = music,
                shakeEnabled = shake,
                notificationsEnabled = notifications
            )
        )
    }

    suspend fun resetAllGameData() = withContext(Dispatchers.IO) {
        dao.clearProfile()
        dao.clearJarStates()
        dao.clearFossilUpgrades()
        dao.clearHistory()
        initializeDefaultsIfNeeded()
    }

    // Helper calculators
    companion object {
        fun parseUpgradeLevels(json: String): Map<String, Int> {
            val map = mutableMapOf<String, Int>()
            try {
                val obj = JSONObject(json)
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    map[k] = obj.optInt(k, 0)
                }
            } catch (_: Exception) {}
            return map
        }

        fun serializeUpgradeLevels(map: Map<String, Int>): String {
            val obj = JSONObject()
            for ((k, v) in map) {
                obj.put(k, v)
            }
            return obj.toString()
        }

        fun calculateJarPopRate(
            eraIndex: Int,
            upgradeLevels: Map<String, Int>,
            jarType: JarType
        ): Double {
            val era = Era.fromIndex(eraIndex)
            var rate = era.basePopPerSec

            for (upg in UpgradeCatalog.POPULATION_UPGRADES) {
                if (upg.eraIndex <= eraIndex) {
                    val level = upgradeLevels[upg.id] ?: 0
                    if (level > 0) {
                        rate += upg.popRateBonus * level
                    }
                }
            }
            return rate
        }

        fun calculateGlobalGrowthMultiplier(fossilUpgrades: Map<String, Int>): Double {
            var mult = 1.0
            val vigorLevel = fossilUpgrades["fd_cellular_vigor"] ?: 0
            mult += vigorLevel * 0.20

            val memoryLevel = fossilUpgrades["fd_dna_memory"] ?: 0
            mult += memoryLevel * 0.15

            val equilibrium = fossilUpgrades["fd_golden_equilibrium"] ?: 0
            mult += equilibrium * 0.10

            return mult
        }

        fun calculateStartingPopulation(fossilUpgrades: Map<String, Int>): Double {
            val infusion = fossilUpgrades["fd_primordial_infusion"] ?: 0
            return infusion * 50.0
        }

        fun calculatePassiveOmPerSec(fossilUpgrades: Map<String, Int>): Double {
            val ambient = fossilUpgrades["fd_ambient_photosynthesis"] ?: 0
            return ambient * 1.0
        }

        fun calculateTapPowerOM(fossilUpgrades: Map<String, Int>): Double {
            val attractor = fossilUpgrades["fd_organic_attractor"] ?: 0
            return 1.0 + (attractor * 1.0)
        }

        fun calculateGreatResetMultiplierGain(
            eraIndex: Int,
            peakPopulation: Double,
            extinctionType: ExtinctionType
        ): Double {
            val baseGain = when {
                eraIndex >= 9 -> 1.00 // +100% (+2.0x compounding) for Ascension
                eraIndex >= 7 -> 0.50 // +50%
                eraIndex >= 5 -> 0.30 // +30%
                eraIndex >= 3 -> 0.15 // +15%
                else -> 0.05 // +5%
            }
            val typeBonus = if (extinctionType == ExtinctionType.PEACEFUL_ASCENSION) 1.5 else 1.0
            return baseGain * typeBonus
        }

        fun calculatePassiveFossilDustRatePerSec(
            fossilUpgrades: Map<String, Int>,
            unlockedJars: List<JarType> = listOf(JarType.MASON),
            activeJarType: JarType = JarType.MASON,
            greatResetMultiplier: Double = 1.0
        ): Double {
            val distilleryLevel = fossilUpgrades["fd_passive_distillery"] ?: 0
            val distilleryRatePerSec = (distilleryLevel * 0.5) / 60.0 // +0.5 FD/min per level

            // Base ambient micro-fossilization rate: each unlocked jar synthesizes base 0.15 FD/min scaled by its individual multiplier
            val unlockedBaseRatePerSec = unlockedJars.sumOf { (0.15 * it.fossilDustMultiplier) / 60.0 }
                .coerceAtLeast((0.15 * activeJarType.fossilDustMultiplier) / 60.0)

            var rate = unlockedBaseRatePerSec + (distilleryRatePerSec * activeJarType.fossilDustMultiplier)

            val catalyst = fossilUpgrades["fd_fossil_catalyst_core"] ?: 0
            rate *= (1.0 + catalyst * 0.25)

            rate *= max(1.0, greatResetMultiplier)
            return rate
        }

        fun calculatePassiveFossilDustPerMinute(
            fossilUpgrades: Map<String, Int>,
            unlockedJars: List<JarType> = listOf(JarType.MASON),
            activeJarType: JarType = JarType.MASON,
            greatResetMultiplier: Double = 1.0
        ): Double {
            return calculatePassiveFossilDustRatePerSec(fossilUpgrades, unlockedJars, activeJarType, greatResetMultiplier) * 60.0
        }

        fun calculateFossilDustEarned(
            peakPopulation: Double,
            eraIndex: Int,
            extinctionType: ExtinctionType,
            fossilUpgrades: Map<String, Int>,
            jarType: JarType = JarType.MASON,
            greatResetMultiplier: Double = 1.0
        ): Double {
            val eraBonus = eraIndex.toDouble().pow(2.2) * 4.0
            val popLog = log10(max(1.0, peakPopulation))
            val popBonus = popLog.pow(1.8) * 2.5

            var dust = (eraBonus + popBonus) * extinctionType.bonusFossilDustMultiplier * jarType.fossilDustMultiplier

            // Fossil upgrades multipliers
            val sieve = fossilUpgrades["fd_sediment_sieve"] ?: 0
            dust *= (1.0 + sieve * 0.30)

            if (eraIndex >= 7) {
                val amber = fossilUpgrades["fd_amber_preservation"] ?: 0
                dust *= (1.0 + amber * 0.50)
            }

            if (extinctionType == ExtinctionType.PEACEFUL_ASCENSION) {
                val beacon = fossilUpgrades["fd_ascension_beacon"] ?: 0
                dust *= (1.0 + beacon * 1.0)
            }

            val catalyst = fossilUpgrades["fd_fossil_catalyst_core"] ?: 0
            dust *= (1.0 + catalyst * 0.25)

            // Multiply by persistent Great Reset Multiplier
            dust *= max(1.0, greatResetMultiplier)

            return max(5.0, floor(dust))
        }
    }
}
