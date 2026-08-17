package com.example.ui

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.billing.BillingConnectionStatus
import com.example.billing.BillingManager
import com.example.data.AchievementCatalog
import com.example.data.AchievementDef
import com.example.data.AppDatabase
import com.example.data.AutomatorType
import com.example.data.CivilizationHistoryEntity
import com.example.data.Era
import com.example.data.ExtinctionType
import com.example.data.FossilBranch
import com.example.data.FossilUpgradeDef
import com.example.data.FossilUpgradeEntity
import com.example.data.GameRepository
import com.example.data.GemPack
import com.example.data.GemPowerUpType
import com.example.data.GemStoreItem
import com.example.data.JarStateEntity
import com.example.data.JarType
import com.example.data.NarrativeChoice
import com.example.data.NarrativeEvent
import com.example.data.NarrativeEventCatalog
import com.example.data.OfflineEarningsResult
import com.example.data.PlayerProfileEntity
import com.example.data.PopulationUpgradeDef
import com.example.data.StoreDefinitions
import com.example.data.UpgradeCatalog
import com.example.sensor.ShakeDetector
import com.example.sound.SoundManager
import com.example.util.NumberFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

enum class ActiveScreen {
    JAR_VIEW,
    UPGRADES_SHOP,
    STORE,
    FOSSIL_RECORD,
    KITCHEN_COUNTER,
    SETTINGS
}

data class FloatingParticle(
    val id: Long,
    val text: String,
    val xOffsetNorm: Float, // -1f to 1f
    val isPop: Boolean
)

data class ActiveExtinctionState(
    val extinctionType: ExtinctionType,
    val yearsSurvived: Double,
    val eraReached: Era,
    val peakPopulation: Double,
    val fossilDustEarned: Double,
    val isAnimating: Boolean,
    val isGreatReset: Boolean = true,
    val greatResetBonusPercent: Double = 0.0,
    val newGreatResetMultiplier: Double = 1.0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application.applicationContext,
        AppDatabase::class.java,
        "civilization_in_a_jar.db"
    ).fallbackToDestructiveMigration().build()

    val repository = GameRepository(db.gameDao())
    val soundManager = SoundManager()
    private var shakeDetector: ShakeDetector? = null
    val billingManager = BillingManager(application.applicationContext, viewModelScope) { amount, source ->
        handleGemsGranted(amount, source)
    }

    // Navigation & Screen State
    private val _currentScreen = MutableStateFlow(ActiveScreen.JAR_VIEW)
    val currentScreen: StateFlow<ActiveScreen> = _currentScreen.asStateFlow()

    // Shop Buy Quantity Mode (1x, 10x, Max)
    private val _buyMultiplier = MutableStateFlow(1)
    val buyMultiplier: StateFlow<Int> = _buyMultiplier.asStateFlow()

    // Active Jar In-Memory Simulation State
    private val _activeJarId = MutableStateFlow(JarType.MASON.id)
    val activeJarId: StateFlow<String> = _activeJarId.asStateFlow()

    private val _activePopulation = MutableStateFlow(0.0)
    val activePopulation: StateFlow<Double> = _activePopulation.asStateFlow()

    private val _activeOrganicMatter = MutableStateFlow(15.0)
    val activeOrganicMatter: StateFlow<Double> = _activeOrganicMatter.asStateFlow()

    private val _activeEraIndex = MutableStateFlow(1)
    val activeEraIndex: StateFlow<Int> = _activeEraIndex.asStateFlow()

    private val _activeInGameYears = MutableStateFlow(0.0)
    val activeInGameYears: StateFlow<Double> = _activeInGameYears.asStateFlow()

    private val _activePeakPop = MutableStateFlow(0.0)
    val activePeakPop: StateFlow<Double> = _activePeakPop.asStateFlow()

    private val _activeUpgradeLevels = MutableStateFlow<Map<String, Int>>(emptyMap())
    val activeUpgradeLevels: StateFlow<Map<String, Int>> = _activeUpgradeLevels.asStateFlow()

    private val _activeNatureMeter = MutableStateFlow(0.0) // For Terrarium
    val activeNatureMeter: StateFlow<Double> = _activeNatureMeter.asStateFlow()

    // Floating text feedback (+1 OM, +5 POP)
    private val _floatingParticles = MutableStateFlow<List<FloatingParticle>>(emptyList())
    val floatingParticles: StateFlow<List<FloatingParticle>> = _floatingParticles.asStateFlow()

    // Earthquake Screen Shake Visual Trigger
    private val _screenShakeTrigger = MutableStateFlow(0L)
    val screenShakeTrigger: StateFlow<Long> = _screenShakeTrigger.asStateFlow()

    // Active Narrative Micro-Event
    private val _activeEvent = MutableStateFlow<NarrativeEvent?>(null)
    val activeEvent: StateFlow<NarrativeEvent?> = _activeEvent.asStateFlow()

    // Event Outcome Notification Banner
    private val _eventOutcomeBanner = MutableStateFlow<String?>(null)
    val eventOutcomeBanner: StateFlow<String?> = _eventOutcomeBanner.asStateFlow()

    // Extinction State
    private val _extinctionState = MutableStateFlow<ActiveExtinctionState?>(null)
    val extinctionState: StateFlow<ActiveExtinctionState?> = _extinctionState.asStateFlow()

    // Offline Earnings Dialog State
    private val _offlineEarnings = MutableStateFlow<OfflineEarningsResult?>(null)
    val offlineEarnings: StateFlow<OfflineEarningsResult?> = _offlineEarnings.asStateFlow()

    // Real-Time Passive Fossil Dust Synthesis Rate (per minute)
    private val _passiveFossilDustRatePerMin = MutableStateFlow(0.0)
    val passiveFossilDustRatePerMin: StateFlow<Double> = _passiveFossilDustRatePerMin.asStateFlow()

    // Confirmation Dialog for Manual Collapse
    private val _showCollapseConfirmDialog = MutableStateFlow(false)
    val showCollapseConfirmDialog: StateFlow<Boolean> = _showCollapseConfirmDialog.asStateFlow()

    // Jar Selection Menu Dialog
    private val _showJarSelectionMenu = MutableStateFlow(false)
    val showJarSelectionMenu: StateFlow<Boolean> = _showJarSelectionMenu.asStateFlow()

    // Achievement Notification Toast
    private val _activeAchievementToast = MutableStateFlow<AchievementDef?>(null)
    val activeAchievementToast: StateFlow<AchievementDef?> = _activeAchievementToast.asStateFlow()

    // Room Flows
    val playerProfile = repository.getPlayerProfile().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )
    val allJars = repository.getAllJarStates().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val fossilUpgrades = repository.getAllFossilUpgrades().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val civilizationHistory = repository.getCivilizationHistory().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    val unlockedAchievements = repository.getUnlockedAchievements().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private var simulationJob: Job? = null
    private var eventTimerSec = 0
    private val random = Random()

    init {
        // Initialize Shake detector
        shakeDetector = ShakeDetector(application.applicationContext) {
            triggerEarthquakeFromShake()
        }

        viewModelScope.launch {
            val offlineRes = repository.initializeDefaultsIfNeeded()
            if (offlineRes != null && offlineRes.elapsedSeconds >= 15) {
                _offlineEarnings.value = offlineRes
                if (offlineRes.fossilDustGained > 0) {
                    soundManager.playUpgradeSound()
                }
            }
            loadActiveJarFromDB()
            startSimulationLoop()
            soundManager.startAmbientMusic()
            shakeDetector?.start()
            updatePassiveRates()
        }

        // Observe profile settings
        viewModelScope.launch {
            playerProfile.collect { profile ->
                if (profile != null) {
                    soundManager.soundEnabled = profile.soundEnabled
                    soundManager.musicEnabled = profile.musicEnabled
                    shakeDetector?.isEnabled = profile.shakeEnabled
                    if (profile.selectedJarId != _activeJarId.value) {
                        _activeJarId.value = profile.selectedJarId
                        loadActiveJarFromDB()
                    }
                    updatePassiveRates()
                }
            }
        }

        // Observe fossil upgrades to keep passive rate synchronized
        viewModelScope.launch {
            fossilUpgrades.collect {
                updatePassiveRates()
            }
        }
    }

    fun updatePassiveRates() {
        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val jars = allJars.value
        val unlockedJarTypes = jars.filter { it.isUnlocked }.map { JarType.fromId(it.jarId) }.ifEmpty { listOf(JarType.MASON) }
        val activeJarType = JarType.fromId(_activeJarId.value)
        val profile = playerProfile.value
        val speedUpMult = if ((profile?.speedUpActiveUntil ?: 0L) > System.currentTimeMillis()) 2.0 else 1.0
        val ratePerMin = GameRepository.calculatePassiveFossilDustPerMinute(
            fossilUpgrades = fossilMap,
            unlockedJars = unlockedJarTypes,
            activeJarType = activeJarType,
            greatResetMultiplier = profile?.greatResetMultiplier ?: 1.0
        ) * speedUpMult
        _passiveFossilDustRatePerMin.value = kotlin.math.round(ratePerMin * 100.0) / 100.0
    }

    /**
     * Calculates offline production based on the time elapsed since the last app open
     * or active session, rewarding players upon returning.
     */
    fun checkAndCalculateOfflineProduction() {
        viewModelScope.launch {
            val offlineRes = repository.calculateOfflineProduction()
            if (offlineRes != null && offlineRes.elapsedSeconds >= 15) {
                _offlineEarnings.value = offlineRes
                if (offlineRes.fossilDustGained > 0) {
                    soundManager.playUpgradeSound()
                }
                loadActiveJarFromDB()
                updatePassiveRates()
            }
        }
    }

    private suspend fun loadActiveJarFromDB() {
        val jar = db.gameDao().getJarStateDirect(_activeJarId.value) ?: return
        _activePopulation.value = jar.population
        _activeOrganicMatter.value = jar.organicMatter
        _activeEraIndex.value = jar.currentEraIndex
        _activeInGameYears.value = jar.totalInGameYears
        _activePeakPop.value = jar.peakPopulation
        _activeNatureMeter.value = jar.natureMeter
        _activeUpgradeLevels.value = GameRepository.parseUpgradeLevels(jar.upgradeLevelsJson)
        soundManager.updateEra(jar.currentEraIndex)
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch(Dispatchers.Default) {
            var tickCount = 0
            while (isActive) {
                delay(100L) // 10 ticks per second (0.1s tick)
                tickCount++

                val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
                val globalMult = GameRepository.calculateGlobalGrowthMultiplier(fossilMap)
                val passiveOm = GameRepository.calculatePassiveOmPerSec(fossilMap)
                val jarType = JarType.fromId(_activeJarId.value)

                // Nature meter synergy for Terrarium
                var terrariumMult = 1.0
                if (jarType == JarType.TERRARIUM) {
                    _activeNatureMeter.value = min(100.0, _activeNatureMeter.value + 0.05)
                    terrariumMult = 1.0 + (_activeNatureMeter.value / 25.0) // up to 5x
                }

                // Calculate base growth rate per sec
                val baseRate = GameRepository.calculateJarPopRate(
                    _activeEraIndex.value,
                    _activeUpgradeLevels.value,
                    jarType
                )
                val speedUpMult = if (System.currentTimeMillis() < (playerProfile.value?.speedUpActiveUntil ?: 0L)) 2.0 else 1.0
                val effectiveRate = baseRate * globalMult * jarType.growthMultiplier * terrariumMult * speedUpMult

                // Check automators
                var autoTapPower = 0.0
                var autoConvertOM = false
                var autoEraAdvance = false

                for (upg in UpgradeCatalog.POPULATION_UPGRADES) {
                    val lvl = _activeUpgradeLevels.value[upg.id] ?: 0
                    if (lvl > 0 && upg.isAutomator) {
                        when (upg.automatorType) {
                            AutomatorType.AUTO_TAP -> autoTapPower += (lvl * 1.5)
                            AutomatorType.AUTO_CONVERT_OM -> autoConvertOM = true
                            AutomatorType.AUTO_ERA_ADVANCE -> autoEraAdvance = true
                            null -> {}
                        }
                    }
                }

                // Add passive OM & Auto-Tap OM
                val omGainPerTick = (passiveOm * 0.1 * jarType.resourceMultiplier) + (autoTapPower * 0.1)
                _activeOrganicMatter.value += omGainPerTick

                // Auto-convert OM into Pop if enabled
                if (autoConvertOM && _activeOrganicMatter.value >= 2.0) {
                    val convertAmount = min(_activeOrganicMatter.value, 1.0)
                    _activeOrganicMatter.value -= convertAmount
                    _activePopulation.value += (convertAmount * 15.0 * globalMult)
                }

                // Add population growth per tick
                val popGainPerTick = effectiveRate * 0.1
                _activePopulation.value += popGainPerTick
                _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)

                // Advance in-game years (1 year every 2.5 seconds)
                _activeInGameYears.value += 0.04

                // Auto-Era advance if unlocked
                if (autoEraAdvance) {
                    val currentEra = Era.fromIndex(_activeEraIndex.value)
                    if (_activeEraIndex.value < 9) {
                        val nextEra = Era.fromIndex(_activeEraIndex.value + 1)
                        if (_activePopulation.value >= nextEra.requiredPopulation) {
                            _activeEraIndex.value = nextEra.index
                            soundManager.updateEra(nextEra.index)
                        }
                    }
                }

                // Background passive growth for OTHER unlocked jars
                if (tickCount % 10 == 0) { // Every 1 second
                    eventTimerSec++
                    // Check Achievements & Milestones every 2 seconds
                    if (eventTimerSec % 2 == 0) {
                        checkMilestonesAndAchievements()
                    }

                    // Check Random Micro Event (every 90-120 seconds or random chance)
                    if (eventTimerSec > 75 && random.nextInt(100) < 6 && _activeEvent.value == null) {
                        triggerRandomNarrativeEvent()
                        eventTimerSec = 0
                    }

                    // Check Random Extinction (only if Era >= 6)
                    if (_activeEraIndex.value >= 6 && _extinctionState.value == null) {
                        val riskReduction = fossilMap["fd_reinforced_silica"] ?: 0
                        val baseExtinctionChance = (_activeEraIndex.value - 5) * 0.00035 * jarType.extinctionRiskMultiplier
                        val adjustedChance = baseExtinctionChance * (1.0 - (riskReduction * 0.10).coerceAtMost(0.8))
                        if (random.nextDouble() < adjustedChance) {
                            triggerRandomExtinction()
                        }
                    }
                }

                // Auto-Save every 5 seconds (50 ticks)
                if (tickCount % 50 == 0) {
                    saveActiveJar()
                }
            }
        }
    }

    fun setScreen(screen: ActiveScreen) {
        _currentScreen.value = screen
    }

    fun setBuyMultiplier(mult: Int) {
        _buyMultiplier.value = mult
    }

    fun openJarSelectionMenu() {
        _showJarSelectionMenu.value = true
        soundManager.playButtonTapSound()
    }

    fun dismissJarSelectionMenu() {
        _showJarSelectionMenu.value = false
    }

    fun selectJar(jarId: String) {
        if (_activeJarId.value == jarId) {
            _showJarSelectionMenu.value = false
            return
        }
        viewModelScope.launch {
            saveActiveJar()
            val newJar = JarType.fromId(jarId)
            repository.selectJar(jarId)
            _activeJarId.value = jarId
            loadActiveJarFromDB()
            _showJarSelectionMenu.value = false
            _currentScreen.value = ActiveScreen.JAR_VIEW
            soundManager.playSwitchJarSound()
            updatePassiveRates()
            showBanner("Switched to ${newJar.displayName}! ✨ ${String.format("%.2f", newJar.fossilDustMultiplier)}x Fossil Dust multiplier active.")
        }
    }

    fun unlockJar(jarType: JarType) {
        viewModelScope.launch {
            val profile = playerProfile.value ?: return@launch
            if (profile.totalFossilDust < jarType.unlockCostFD) {
                showBanner("⚠️ Need ${NumberFormatter.format(jarType.unlockCostFD)} Fossil Dust to unlock ${jarType.displayName}!")
                return@launch
            }
            repository.unlockJar(jarType, jarType.unlockCostFD)
            soundManager.playPurchaseSuccessSound()
            showBanner("🎉 ${jarType.displayName} Unlocked! Active multiplier: ✨ ${String.format("%.2f", jarType.fossilDustMultiplier)}x Fossil Dust.")
            selectJar(jarType.id)
            updatePassiveRates()
            checkMilestonesAndAchievements()
        }
    }

    fun onJarTapped() {
        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val tapPower = GameRepository.calculateTapPowerOM(fossilMap)
        val jarType = JarType.fromId(_activeJarId.value)

        val gainedOM = tapPower * jarType.resourceMultiplier
        _activeOrganicMatter.value += gainedOM

        // If in Era 1, converting OM directly adds first cells
        if (_activeEraIndex.value == 1 && _activePopulation.value < 100.0) {
            _activePopulation.value += 1.0
            _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)
        }

        soundManager.playTapSound()

        // Spawn floating particle
        val xNorm = (random.nextFloat() * 1.4f) - 0.7f
        val particle = FloatingParticle(
            id = System.currentTimeMillis() + random.nextInt(1000),
            text = "+${if (gainedOM >= 10) gainedOM.toInt() else String.format("%.1f", gainedOM)} OM",
            xOffsetNorm = xNorm,
            isPop = false
        )
        _floatingParticles.value = _floatingParticles.value.takeLast(6) + particle

        // Cleanup old particle after delay
        viewModelScope.launch {
            delay(1200)
            _floatingParticles.value = _floatingParticles.value.filter { it.id != particle.id }
        }
    }

    fun canAdvanceEra(): Boolean {
        if (_activeEraIndex.value >= 9) return false
        val nextEra = Era.fromIndex(_activeEraIndex.value + 1)
        return _activePopulation.value >= nextEra.requiredPopulation
    }

    fun canAdvanceEraWithFossilDust(): Boolean {
        return canEvolveEra()
    }

    /**
     * Checks if the civilization can evolve to the next era based on accumulated Fossil Dust.
     * Tracks all 9 distinct eras.
     */
    fun canEvolveEra(): Boolean {
        if (_activeEraIndex.value >= 9) return false
        val nextEra = Era.fromIndex(_activeEraIndex.value + 1)
        val currentFossilDust = playerProfile.value?.totalFossilDust ?: 0.0
        return currentFossilDust >= nextEra.requiredFossilDust
    }

    /**
     * Returns the exact amount of Fossil Dust required to evolve to the next era (out of 9 distinct eras).
     */
    fun getRequiredFossilDustForNextEra(): Double? {
        if (_activeEraIndex.value >= 9) return null
        return Era.fromIndex(_activeEraIndex.value + 1).requiredFossilDust
    }

    fun getNextEraFossilDustMilestone(): Double? {
        return getRequiredFossilDustForNextEra()
    }

    /**
     * Triggers the cataclysmic extinction event effect that accompanies an epochal era shift.
     * Generates jar shockwaves, extinction rumbling audio, and floating particle fallout.
     */
    fun triggerEraExtinctionEventEffect(targetEraIndex: Int) {
        val targetEra = Era.fromIndex(targetEraIndex)
        _screenShakeTrigger.value = System.currentTimeMillis()

        // Play extinction cataclysm sound followed by evolution fanfare
        soundManager.playEarthquakeSound()
        soundManager.playExtinctionSound("Epoch Extinction: ${targetEra.title}")

        // Spawn dramatic extinction and evolution particles
        val p1 = FloatingParticle(
            id = System.currentTimeMillis() + 1,
            text = "💥 EPOCH EXTINCTION EVENT!",
            xOffsetNorm = -0.3f,
            isPop = true
        )
        val p2 = FloatingParticle(
            id = System.currentTimeMillis() + 2,
            text = "✨ EVOLVED TO ERA $targetEraIndex: ${targetEra.title.uppercase()}!",
            xOffsetNorm = 0.3f,
            isPop = true
        )
        _floatingParticles.value = _floatingParticles.value.takeLast(4) + listOf(p1, p2)

        viewModelScope.launch {
            delay(2000)
            _floatingParticles.value = _floatingParticles.value.filter { it.id != p1.id && it.id != p2.id }
        }
    }

    /**
     * Evolves the civilization to the next era (tracking 9 distinct eras),
     * requiring a specific amount of fossil dust to evolve and triggering an extinction event effect.
     */
    fun evolveEra(): Boolean {
        if (!canEvolveEra()) {
            val nextEra = if (_activeEraIndex.value < 9) Era.fromIndex(_activeEraIndex.value + 1) else null
            if (nextEra != null) {
                val currentFossilDust = playerProfile.value?.totalFossilDust ?: 0.0
                val remaining = (nextEra.requiredFossilDust - currentFossilDust).coerceAtLeast(0.0)
                showBanner("⚠️ Requires ${NumberFormatter.format(remaining)} more Fossil Dust to evolve to ${nextEra.title}!")
            }
            return false
        }

        val nextEra = Era.fromIndex(_activeEraIndex.value + 1)
        _activeEraIndex.value = nextEra.index

        // Jumpstart population baseline for the newly unlocked epoch
        val jumpStartPop = max(_activePopulation.value, nextEra.requiredPopulation * 0.08)
        _activePopulation.value = jumpStartPop
        _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)

        // Reward catalyst biomass
        val bonusOM = nextEra.index * 35.0
        _activeOrganicMatter.value += bonusOM

        // Reward trickle gems
        val gemReward = if (nextEra.index >= 8) 10L else 5L
        viewModelScope.launch {
            repository.addGems(gemReward)
        }

        // Trigger the epoch extinction event effect!
        triggerEraExtinctionEventEffect(nextEra.index)

        // Audio & visual fanfare
        soundManager.updateEra(nextEra.index)
        soundManager.playEraFanfare()
        soundManager.playGemSound()
        showBanner("🌌 Cataclysmic Extinction & Evolution! Reborn in Era ${nextEra.index}: ${nextEra.title}! (+$gemReward 💎 Gems)")

        saveActiveJar()
        triggerEraTransitionNarrative(nextEra.index)
        checkMilestonesAndAchievements()
        return true
    }

    /**
     * Handles civilizational era progression catalyzed by accumulated Fossil Dust milestones.
     */
    fun progressEraBasedOnFossilDustMilestones(): Boolean {
        return evolveEra()
    }

    fun advanceEra() {
        if (canAdvanceEra() || canEvolveEra()) {
            evolveEra()
        }
    }

    fun buyPopulationUpgrade(upgradeDef: PopulationUpgradeDef) {
        val currentLevel = _activeUpgradeLevels.value[upgradeDef.id] ?: 0
        val timesToBuy = calculatePurchaseCount(upgradeDef, currentLevel, _buyMultiplier.value)
        if (timesToBuy <= 0) return

        var totalCostPOP = 0.0
        var totalCostOM = 0.0
        for (i in 0 until timesToBuy) {
            val lvl = currentLevel + i
            totalCostPOP += upgradeDef.baseCostPOP * (upgradeDef.costMultiplier.pow(lvl.toDouble()))
            if (upgradeDef.baseCostOM > 0) {
                totalCostOM += upgradeDef.baseCostOM * (upgradeDef.costMultiplier.pow(lvl.toDouble()))
            }
        }

        if (_activePopulation.value >= totalCostPOP && _activeOrganicMatter.value >= totalCostOM) {
            _activePopulation.value -= totalCostPOP
            _activeOrganicMatter.value -= totalCostOM

            val newLevels = _activeUpgradeLevels.value.toMutableMap()
            newLevels[upgradeDef.id] = currentLevel + timesToBuy
            _activeUpgradeLevels.value = newLevels

            soundManager.playUpgradeSound()
            saveActiveJar()
        }
    }

    fun calculatePurchaseCount(upgradeDef: PopulationUpgradeDef, currentLevel: Int, desiredMultiplier: Int): Int {
        if (desiredMultiplier == 1) {
            val cost = upgradeDef.baseCostPOP * (upgradeDef.costMultiplier.pow(currentLevel.toDouble()))
            val omCost = if (upgradeDef.baseCostOM > 0) upgradeDef.baseCostOM * (upgradeDef.costMultiplier.pow(currentLevel.toDouble())) else 0.0
            return if (_activePopulation.value >= cost && _activeOrganicMatter.value >= omCost) 1 else 0
        }

        if (desiredMultiplier == 10) {
            var tempCost = 0.0
            var tempOMCost = 0.0
            for (i in 0 until 10) {
                val lvl = currentLevel + i
                tempCost += upgradeDef.baseCostPOP * (upgradeDef.costMultiplier.pow(lvl.toDouble()))
                if (upgradeDef.baseCostOM > 0) tempOMCost += upgradeDef.baseCostOM * (upgradeDef.costMultiplier.pow(lvl.toDouble()))
            }
            return if (_activePopulation.value >= tempCost && _activeOrganicMatter.value >= tempOMCost) 10 else 0
        }

        // Max mode
        var count = 0
        var tempPop = _activePopulation.value
        var tempOM = _activeOrganicMatter.value
        while (count < 100) {
            val lvl = currentLevel + count
            val cost = upgradeDef.baseCostPOP * (upgradeDef.costMultiplier.pow(lvl.toDouble()))
            val omCost = if (upgradeDef.baseCostOM > 0) upgradeDef.baseCostOM * (upgradeDef.costMultiplier.pow(lvl.toDouble())) else 0.0
            if (tempPop >= cost && tempOM >= omCost) {
                tempPop -= cost
                tempOM -= omCost
                count++
            } else {
                break
            }
        }
        return count
    }

    fun buyFossilUpgrade(upgradeDef: FossilUpgradeDef) {
        val currentLevel = fossilUpgrades.value.find { it.upgradeId == upgradeDef.id }?.level ?: 0
        if (currentLevel >= upgradeDef.maxLevel) return

        viewModelScope.launch {
            repository.buyFossilUpgrade(upgradeDef, currentLevel)
            soundManager.playUpgradeSound()
        }
    }

    fun requestCollapseCivilization() {
        _showCollapseConfirmDialog.value = true
    }

    fun dismissCollapseConfirm() {
        _showCollapseConfirmDialog.value = false
    }

    fun calculateEstimatedCollapseDust(): Double {
        val currentMult = playerProfile.value?.greatResetMultiplier ?: 1.0
        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val estType = if (_activeEraIndex.value >= 9) ExtinctionType.PEACEFUL_ASCENSION else ExtinctionType.RESOURCE_COLLAPSE
        val activeJar = JarType.fromId(_activeJarId.value)
        return GameRepository.calculateFossilDustEarned(
            peakPopulation = _activePeakPop.value,
            eraIndex = _activeEraIndex.value,
            extinctionType = estType,
            fossilUpgrades = fossilMap,
            jarType = activeJar,
            greatResetMultiplier = currentMult
        )
    }

    fun calculateEstimatedGreatResetBonus(): Double {
        val estType = if (_activeEraIndex.value >= 9) ExtinctionType.PEACEFUL_ASCENSION else ExtinctionType.RESOURCE_COLLAPSE
        return GameRepository.calculateGreatResetMultiplierGain(
            _activeEraIndex.value,
            _activePeakPop.value,
            estType
        )
    }

    /**
     * Triggers a Great Reset Extinction event that resets civilization progress
     * in exchange for permanent Fossil Dust and a persistent Great Reset multiplier bonus.
     */
    fun executeGreatResetExtinction() {
        _showCollapseConfirmDialog.value = false
        val extinctionType = if (_activeEraIndex.value >= 9) {
            ExtinctionType.PEACEFUL_ASCENSION
        } else {
            ExtinctionType.entries.filter { it.minEra <= _activeEraIndex.value }.random()
        }
        triggerExtinctionWithType(extinctionType, isGreatReset = true)
    }

    fun executeManualCollapse() {
        executeGreatResetExtinction()
    }

    fun triggerExtinctionEvent(type: ExtinctionType? = null, isGreatReset: Boolean = true) {
        val chosenType = type ?: if (_activeEraIndex.value >= 9) {
            ExtinctionType.PEACEFUL_ASCENSION
        } else {
            ExtinctionType.entries.filter { it.minEra <= _activeEraIndex.value }.random()
        }
        triggerExtinctionWithType(chosenType, isGreatReset = isGreatReset)
    }

    private fun triggerRandomExtinction() {
        viewModelScope.launch {
            val protected = repository.consumeExtinctionInsurance()
            if (protected) {
                soundManager.playShieldSound()
                showBanner("🛡️ Extinction Insurance Barrier Absorbed the Cataclysm! Civilization Saved!")
                return@launch
            }
            val possible = ExtinctionType.entries.filter { it.minEra <= _activeEraIndex.value && it != ExtinctionType.PEACEFUL_ASCENSION }
            val chosen = possible.random()
            triggerExtinctionWithType(chosen, isGreatReset = true)
        }
    }

    private fun triggerExtinctionWithType(type: ExtinctionType, isGreatReset: Boolean = true) {
        val currentResetMult = playerProfile.value?.greatResetMultiplier ?: 1.0
        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val activeJar = JarType.fromId(_activeJarId.value)
        val dustEarned = GameRepository.calculateFossilDustEarned(
            peakPopulation = _activePeakPop.value,
            eraIndex = _activeEraIndex.value,
            extinctionType = type,
            fossilUpgrades = fossilMap,
            jarType = activeJar,
            greatResetMultiplier = currentResetMult
        )

        val resetBonusGain = if (isGreatReset) {
            GameRepository.calculateGreatResetMultiplierGain(_activeEraIndex.value, _activePeakPop.value, type)
        } else 0.0

        val newTotalMult = currentResetMult * (1.0 + resetBonusGain)
        val era = Era.fromIndex(_activeEraIndex.value)

        val extState = ActiveExtinctionState(
            extinctionType = type,
            yearsSurvived = _activeInGameYears.value,
            eraReached = era,
            peakPopulation = _activePeakPop.value,
            fossilDustEarned = dustEarned,
            isAnimating = true,
            isGreatReset = isGreatReset,
            greatResetBonusPercent = resetBonusGain,
            newGreatResetMultiplier = newTotalMult
        )
        _extinctionState.value = extState

        soundManager.playExtinctionSound(type.displayName)

        // Run dramatic collapse sequence (3.5 seconds animation)
        viewModelScope.launch {
            delay(3500)
            _extinctionState.value = extState.copy(isAnimating = false)
        }
    }

    fun dismissExtinctionAndRebirth() {
        val extState = _extinctionState.value ?: return
        _extinctionState.value = null

        viewModelScope.launch {
            repository.triggerExtinction(
                jarId = _activeJarId.value,
                extinctionType = extState.extinctionType,
                customFossilDustEarned = extState.fossilDustEarned,
                greatResetMultiplierGain = extState.greatResetBonusPercent
            )
            loadActiveJarFromDB()
            val bonusMsg = if (extState.greatResetBonusPercent > 0) {
                " Great Reset Bonus: ${(extState.greatResetBonusPercent * 100).toInt()}% (+${String.format("%.2f", extState.newGreatResetMultiplier)}x global multiplier)!"
            } else ""
            showBanner("⚡ Rebirth Complete! +${extState.fossilDustEarned.toInt()} Fossil Dust harvested.$bonusMsg")
            checkMilestonesAndAchievements()
        }
    }

    fun triggerEarthquakeFromShake() {
        _screenShakeTrigger.value = System.currentTimeMillis()
        soundManager.playEarthquakeSound()

        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val shockResist = fossilMap["fd_shock_absorbers"] ?: 0
        val isBonus = random.nextDouble() < (0.45 + (shockResist * 0.15))

        if (isBonus) {
            val omBonus = (20.0 + random.nextInt(40)) * JarType.fromId(_activeJarId.value).resourceMultiplier
            _activeOrganicMatter.value += omBonus
            showBanner("🌋 Earthquake shook loose +${omBonus.toInt()} Organic Matter!")
        } else {
            val popLoss = _activePopulation.value * 0.05
            _activePopulation.value = max(0.0, _activePopulation.value - popLoss)
            showBanner("🌋 Earthquake tremor caused minor sediment settling.")
        }
    }

    fun getAvailableNarrativeEventsForCurrentEra(): List<NarrativeEvent> {
        return NarrativeEventCatalog.getEventsForEra(_activeEraIndex.value)
    }

    /**
     * Triggers a narrative micro-event based on the current era or an explicitly requested event.
     */
    fun triggerNarrativeMicroEvent(eventId: String? = null, forceEra: Int? = null) {
        if (eventId != null) {
            val event = NarrativeEventCatalog.EVENTS.find { it.id == eventId }
            if (event != null) {
                _activeEvent.value = event
                return
            }
        }
        val targetEra = forceEra ?: _activeEraIndex.value
        val event = NarrativeEventCatalog.getRandomEventForEra(targetEra, excludeId = _activeEvent.value?.id)
        if (event != null) {
            _activeEvent.value = event
        }
    }

    /**
     * Triggers an era-transition specific narrative micro-event when evolving to a new era.
     */
    fun triggerEraTransitionNarrative(eraIndex: Int) {
        viewModelScope.launch {
            delay(1200) // Brief delay after evolution fanfare for dramatic narrative timing
            if (_activeEvent.value == null && _extinctionState.value == null) {
                val eraEvents = NarrativeEventCatalog.getEventsForEra(eraIndex)
                if (eraEvents.isNotEmpty()) {
                    _activeEvent.value = eraEvents.random()
                }
            }
        }
    }

    private fun triggerRandomNarrativeEvent() {
        val event = NarrativeEventCatalog.getRandomEventForEra(_activeEraIndex.value, excludeId = _activeEvent.value?.id)
        if (event != null) {
            _activeEvent.value = event
        }
    }

    fun resolveNarrativeChoice(choice: NarrativeChoice) {
        val event = _activeEvent.value ?: return
        _activeEvent.value = null

        val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
        val rewardMult = 1.0 + ((fossilMap["fd_enlightened_diplomacy"] ?: 0) * 0.35)

        if (choice.popBonusPercent > 0) {
            val boost = _activePopulation.value * choice.popBonusPercent * rewardMult
            _activePopulation.value += boost
            _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)
        }
        if (choice.omBonus > 0) {
            _activeOrganicMatter.value += (choice.omBonus * rewardMult)
        }
        if (choice.fossilDustBonus > 0) {
            viewModelScope.launch {
                val profile = db.gameDao().getPlayerProfileDirect()
                if (profile != null) {
                    val fd = choice.fossilDustBonus * rewardMult
                    db.gameDao().insertOrUpdateProfile(profile.copy(
                        totalFossilDust = profile.totalFossilDust + fd,
                        lifetimeFossilDust = profile.lifetimeFossilDust + fd
                    ))
                }
            }
        }
        if (choice.gemBonus > 0) {
            viewModelScope.launch {
                repository.addGems(choice.gemBonus)
                soundManager.playGemSound()
            }
        }

        soundManager.playUpgradeSound()
        showBanner(choice.outcomeNarrative)
        saveActiveJar()
    }

    // Billing & Gem Store operations
    fun handleGemsGranted(amount: Long, source: String) {
        viewModelScope.launch {
            repository.addGems(amount)
            soundManager.playGemSound()
            showBanner("💎 +$amount Gems received ($source)!")
        }
    }

    fun purchaseGemPack(activity: Activity, pack: GemPack) {
        billingManager.purchaseGemPack(activity, pack)
    }

    fun retryBillingConnection() {
        billingManager.startConnection()
    }

    fun getLocalizedPrice(pack: GemPack): String {
        return billingManager.getLocalizedPrice(pack)
    }

    fun buyGemStoreItem(item: GemStoreItem) {
        viewModelScope.launch {
            val success = repository.spendGems(item.gemCost)
            if (!success) {
                showBanner("Not enough Gems! You need ${item.gemCost} 💎.")
                return@launch
            }

            when (item.type) {
                GemPowerUpType.INSTANT_GROWTH_30M -> {
                    val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
                    val globalMult = GameRepository.calculateGlobalGrowthMultiplier(fossilMap)
                    val passiveOm = GameRepository.calculatePassiveOmPerSec(fossilMap)
                    val jarType = JarType.fromId(_activeJarId.value)
                    val terrariumMult = if (jarType == JarType.TERRARIUM) 1.0 + (_activeNatureMeter.value / 25.0) else 1.0
                    val baseRate = GameRepository.calculateJarPopRate(_activeEraIndex.value, _activeUpgradeLevels.value, jarType)
                    val effectiveRate = baseRate * globalMult * jarType.growthMultiplier * terrariumMult
                    val popGained = (effectiveRate * 1800.0).coerceAtLeast(100.0)
                    val omGained = (passiveOm * jarType.resourceMultiplier * 1800.0) + (popGained * 0.1)

                    _activePopulation.value += popGained
                    _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)
                    _activeOrganicMatter.value += omGained

                    soundManager.playPurchaseSuccessSound()
                    showBanner("⚡ Instant Growth (30m)! +${com.example.util.NumberFormatter.format(popGained)} Pop, +${com.example.util.NumberFormatter.format(omGained)} OM!")
                    saveActiveJar()
                }

                GemPowerUpType.INSTANT_GROWTH_2H -> {
                    val fossilMap = fossilUpgrades.value.associate { it.upgradeId to it.level }
                    val globalMult = GameRepository.calculateGlobalGrowthMultiplier(fossilMap)
                    val passiveOm = GameRepository.calculatePassiveOmPerSec(fossilMap)
                    val jarType = JarType.fromId(_activeJarId.value)
                    val terrariumMult = if (jarType == JarType.TERRARIUM) 1.0 + (_activeNatureMeter.value / 25.0) else 1.0
                    val baseRate = GameRepository.calculateJarPopRate(_activeEraIndex.value, _activeUpgradeLevels.value, jarType)
                    val effectiveRate = baseRate * globalMult * jarType.growthMultiplier * terrariumMult
                    val popGained = (effectiveRate * 7200.0).coerceAtLeast(500.0)
                    val omGained = (passiveOm * jarType.resourceMultiplier * 7200.0) + (popGained * 0.15)

                    _activePopulation.value += popGained
                    _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)
                    _activeOrganicMatter.value += omGained

                    soundManager.playPurchaseSuccessSound()
                    showBanner("⏳ Super Epoch Surge (2h)! +${com.example.util.NumberFormatter.format(popGained)} Pop, +${com.example.util.NumberFormatter.format(omGained)} OM!")
                    saveActiveJar()
                }

                GemPowerUpType.SPEED_UP_30M -> {
                    repository.applySpeedUp(30 * 60 * 1000L)
                    soundManager.playPurchaseSuccessSound()
                    showBanner("🚀 2x Speed-Up Boost Activated for 30 minutes across all jars!")
                }

                GemPowerUpType.EXTINCTION_INSURANCE -> {
                    repository.addExtinctionInsurance(1)
                    soundManager.playShieldSound()
                    showBanner("🛡️ Extinction Insurance Shield Acquired (+1 Shield Active)!")
                }

                GemPowerUpType.INSTANT_ERA_UNLOCK -> {
                    if (_activeEraIndex.value < 9) {
                        val nextEra = Era.fromIndex(_activeEraIndex.value + 1)
                        _activeEraIndex.value = nextEra.index
                        val jumpStartPop = max(_activePopulation.value, nextEra.requiredPopulation * 0.10)
                        _activePopulation.value = jumpStartPop
                        _activePeakPop.value = max(_activePeakPop.value, _activePopulation.value)
                        _activeOrganicMatter.value += (nextEra.index * 60.0)
                        soundManager.updateEra(nextEra.index)
                        soundManager.playEraFanfare()
                        showBanner("🌌 Instant Era Catalyst! Evolved to ${nextEra.title}!")
                        saveActiveJar()
                        triggerEraTransitionNarrative(nextEra.index)
                        checkMilestonesAndAchievements()
                    } else {
                        repository.addGems(item.gemCost)
                        showBanner("Max Era reached! Era Catalyst is not needed.")
                    }
                }

                GemPowerUpType.COSMETIC_THEME -> {
                    if (item.themeId != null) {
                        repository.unlockAndSelectJarTheme(item.themeId)
                        soundManager.playPurchaseSuccessSound()
                        showBanner("🎨 ${item.title} Theme Unlocked & Equipped!")
                    }
                }
            }
        }
    }

    fun selectCosmeticTheme(themeId: String) {
        viewModelScope.launch {
            repository.selectJarTheme(themeId)
            soundManager.playTapSound()
            showBanner("Jar Theme Updated!")
        }
    }

    fun dismissEvent() {
        _activeEvent.value = null
    }

    fun dismissOfflineEarnings() {
        val earnings = _offlineEarnings.value
        _offlineEarnings.value = null
        if (earnings != null) {
            soundManager.playPurchaseSuccessSound()
            if (earnings.fossilDustGained > 0) {
                showBanner("✨ Claimed +${com.example.util.NumberFormatter.format(earnings.fossilDustGained)} Fossil Dust from idle synthesis!")
            } else {
                showBanner("🌱 Welcome back! Your civilizations have continued evolving.")
            }
            saveActiveJar()
        }
    }

    fun dismissAchievementToast() {
        _activeAchievementToast.value = null
    }

    fun checkMilestonesAndAchievements() {
        viewModelScope.launch {
            val unlockedIds = repository.getUnlockedAchievementsDirect().map { it.achievementId }.toSet()
            val profile = playerProfile.value ?: repository.getPlayerProfileDirect()
            val jars = allJars.value.ifEmpty { repository.getAllJarStatesDirect() }
            val currentEra = _activeEraIndex.value
            val currentPeakPop = max(_activePeakPop.value, _activePopulation.value)
            val lifetimeFD = profile?.lifetimeFossilDust ?: 0.0
            val totalFD = profile?.totalFossilDust ?: 0.0
            val greatResets = profile?.greatResetCount ?: 0

            // Helper to unlock if not yet unlocked
            fun evaluate(achId: String, condition: Boolean) {
                if (condition && !unlockedIds.contains(achId)) {
                    val def = AchievementCatalog.getById(achId) ?: return
                    viewModelScope.launch {
                        val newlyUnlocked = repository.unlockAchievement(achId, def.rewardFossilDust)
                        if (newlyUnlocked) {
                            soundManager.playAchievementSound()
                            _activeAchievementToast.value = def
                            delay(4500)
                            if (_activeAchievementToast.value?.id == def.id) {
                                _activeAchievementToast.value = null
                            }
                        }
                    }
                }
            }

            // Era Milestones
            evaluate("ach_era_2", currentEra >= 2)
            evaluate("ach_era_3", currentEra >= 3)
            evaluate("ach_era_5", currentEra >= 5)
            evaluate("ach_era_7", currentEra >= 7)
            evaluate("ach_era_8", currentEra >= 8)
            evaluate("ach_era_9", currentEra >= 9)

            // Population Milestones
            evaluate("ach_pop_1k", currentPeakPop >= 1_000.0)
            evaluate("ach_pop_1m", currentPeakPop >= 1_000_000.0)
            evaluate("ach_pop_1b", currentPeakPop >= 1_000_000_000.0)
            evaluate("ach_pop_1t", currentPeakPop >= 1_000_000_000_000.0)

            // Fossil Dust Milestones
            evaluate("ach_fd_100", (totalFD >= 100.0 || lifetimeFD >= 100.0))
            evaluate("ach_fd_10k", (totalFD >= 10_000.0 || lifetimeFD >= 10_000.0))
            evaluate("ach_fd_1m", (totalFD >= 1_000_000.0 || lifetimeFD >= 1_000_000.0))

            // Great Reset Milestones
            evaluate("ach_reset_1", greatResets >= 1)
            evaluate("ach_reset_5", greatResets >= 5)

            // Biome Unlocks
            val spiceUnlocked = jars.any { it.jarId == JarType.SPICE.id && it.isUnlocked }
            val terrariumUnlocked = jars.any { it.jarId == JarType.TERRARIUM.id && it.isUnlocked }
            evaluate("ach_unlock_spice", spiceUnlocked)
            evaluate("ach_unlock_terrarium", terrariumUnlocked)
        }
    }

    private fun showBanner(msg: String) {
        _eventOutcomeBanner.value = msg
        viewModelScope.launch {
            delay(3500)
            if (_eventOutcomeBanner.value == msg) {
                _eventOutcomeBanner.value = null
            }
        }
    }

    private fun saveActiveJar() {
        viewModelScope.launch {
            repository.saveGameTick(
                jarId = _activeJarId.value,
                population = _activePopulation.value,
                organicMatter = _activeOrganicMatter.value,
                peakPopulation = _activePeakPop.value,
                inGameYears = _activeInGameYears.value,
                eraIndex = _activeEraIndex.value,
                natureMeter = _activeNatureMeter.value,
                upgradeLevelsJson = GameRepository.serializeUpgradeLevels(_activeUpgradeLevels.value)
            )
        }
    }

    fun updateSettings(sound: Boolean, music: Boolean, shake: Boolean, notifications: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(sound, music, shake, notifications)
        }
    }

    fun resetAllGameProgress() {
        viewModelScope.launch {
            repository.resetAllGameData()
            loadActiveJarFromDB()
            _currentScreen.value = ActiveScreen.JAR_VIEW
            showBanner("Game progress has been completely reset.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveActiveJar()
        simulationJob?.cancel()
        soundManager.destroy()
        shakeDetector?.stop()
        billingManager.destroy()
    }
}
