package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.GameRepository
import com.example.data.JarType
import com.example.ui.ActiveScreen
import com.example.ui.GameViewModel
import com.example.ui.components.AchievementNotificationToast
import com.example.ui.components.CollapseConfirmDialog
import com.example.ui.components.ExtinctionDialog
import com.example.ui.components.JarSelectionMenuDialog
import com.example.ui.components.NarrativeEventDialog
import com.example.ui.components.OfflineEarningsDialog
import com.example.ui.screens.FossilRecordScreen
import com.example.ui.screens.KitchenCounterScreen
import com.example.ui.screens.MainJarScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.theme.CivilizationInAJarTheme

import androidx.activity.viewModels

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CivilizationInAJarTheme {
                CivilizationGameApp(viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkAndCalculateOfflineProduction()
    }
}

@Composable
fun CivilizationGameApp(viewModel: GameViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeJarId by viewModel.activeJarId.collectAsState()
    val population by viewModel.activePopulation.collectAsState()
    val organicMatter by viewModel.activeOrganicMatter.collectAsState()
    val eraIndex by viewModel.activeEraIndex.collectAsState()
    val inGameYears by viewModel.activeInGameYears.collectAsState()
    val peakPop by viewModel.activePeakPop.collectAsState()
    val natureMeter by viewModel.activeNatureMeter.collectAsState()
    val upgradeLevels by viewModel.activeUpgradeLevels.collectAsState()
    val floatingParticles by viewModel.floatingParticles.collectAsState()
    val shakeTrigger by viewModel.screenShakeTrigger.collectAsState()
    val activeEvent by viewModel.activeEvent.collectAsState()
    val eventOutcomeBanner by viewModel.eventOutcomeBanner.collectAsState()
    val extinctionState by viewModel.extinctionState.collectAsState()
    val offlineEarnings by viewModel.offlineEarnings.collectAsState()
    val showCollapseConfirm by viewModel.showCollapseConfirmDialog.collectAsState()
    val showJarSelectionMenu by viewModel.showJarSelectionMenu.collectAsState()
    val buyMultiplier by viewModel.buyMultiplier.collectAsState()
    val achievementToast by viewModel.activeAchievementToast.collectAsState()
    val passiveDustRate by viewModel.passiveFossilDustRatePerMin.collectAsState()

    val profile by viewModel.playerProfile.collectAsState()
    val allJars by viewModel.allJars.collectAsState()
    val fossilUpgrades by viewModel.fossilUpgrades.collectAsState()
    val history by viewModel.civilizationHistory.collectAsState()
    val unlockedAchievements by viewModel.unlockedAchievements.collectAsState()

    val billingStatus by viewModel.billingManager.connectionStatus.collectAsState()
    val isPurchasePending by viewModel.billingManager.isPurchasePending.collectAsState()
    val billingMessage by viewModel.billingManager.billingMessage.collectAsState()

    val fossilDust = profile?.totalFossilDust ?: 0.0
    val gems = profile?.gems ?: 0L
    val fossilUpgradesMap = fossilUpgrades.associate { it.upgradeId to it.level }
    val jarType = JarType.fromId(activeJarId)

    // Back handling: Return to Jar view if on another screen
    BackHandler(enabled = currentScreen != ActiveScreen.JAR_VIEW) {
        viewModel.setScreen(ActiveScreen.JAR_VIEW)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                ActiveScreen.JAR_VIEW -> {
                    MainJarScreen(
                        jarType = jarType,
                        eraIndex = eraIndex,
                        population = population,
                        organicMatter = organicMatter,
                        inGameYears = inGameYears,
                        natureMeter = natureMeter,
                        fossilDust = fossilDust,
                        gems = gems,
                        speedUpActiveUntil = profile?.speedUpActiveUntil ?: 0L,
                        jarTheme = profile?.selectedJarTheme ?: "default",
                        upgradeLevels = upgradeLevels,
                        fossilUpgradesMap = fossilUpgradesMap,
                        floatingParticles = floatingParticles,
                        shakeTrigger = shakeTrigger,
                        bannerMessage = eventOutcomeBanner,
                        onJarTapped = { viewModel.onJarTapped() },
                        onAdvanceEra = { viewModel.advanceEra() },
                        canAdvanceEra = viewModel.canAdvanceEra(),
                        canAdvanceEraWithFossilDust = viewModel.canAdvanceEraWithFossilDust(),
                        onAdvanceEraWithFossilDust = { viewModel.progressEraBasedOnFossilDustMilestones() },
                        onOpenShop = { viewModel.setScreen(ActiveScreen.UPGRADES_SHOP) },
                        onOpenStore = { viewModel.setScreen(ActiveScreen.STORE) },
                        onOpenFossilRecord = { viewModel.setScreen(ActiveScreen.FOSSIL_RECORD) },
                        onOpenKitchenCounter = { viewModel.setScreen(ActiveScreen.KITCHEN_COUNTER) },
                        onOpenJarSelectionMenu = { viewModel.openJarSelectionMenu() },
                        onOpenSettings = { viewModel.setScreen(ActiveScreen.SETTINGS) },
                        onCollapseRequested = { viewModel.requestCollapseCivilization() }
                    )
                }

                ActiveScreen.STORE -> {
                    StoreScreen(
                        profile = profile,
                        billingStatus = billingStatus,
                        isPurchasePending = isPurchasePending,
                        billingMessage = billingMessage,
                        onBuyRealMoneyPack = { activity, pack -> viewModel.purchaseGemPack(activity, pack) },
                        onBuyGemStoreItem = { item -> viewModel.buyGemStoreItem(item) },
                        onSelectJarTheme = { themeId -> viewModel.selectCosmeticTheme(themeId) },
                        onRetryBillingConnection = { viewModel.retryBillingConnection() },
                        getLocalizedPrice = { pack -> viewModel.getLocalizedPrice(pack) },
                        onBackToJar = { viewModel.setScreen(ActiveScreen.JAR_VIEW) }
                    )
                }

                ActiveScreen.UPGRADES_SHOP -> {
                    ShopScreen(
                        currentEraIndex = eraIndex,
                        population = population,
                        organicMatter = organicMatter,
                        upgradeLevels = upgradeLevels,
                        buyMultiplier = buyMultiplier,
                        onSetBuyMultiplier = { viewModel.setBuyMultiplier(it) },
                        onBuyUpgrade = { viewModel.buyPopulationUpgrade(it) },
                        calculatePurchaseCount = { def, lvl, mult -> viewModel.calculatePurchaseCount(def, lvl, mult) },
                        onBackToJar = { viewModel.setScreen(ActiveScreen.JAR_VIEW) }
                    )
                }

                ActiveScreen.FOSSIL_RECORD -> {
                    FossilRecordScreen(
                        fossilDust = fossilDust,
                        fossilUpgrades = fossilUpgrades,
                        civilizationHistory = history,
                        unlockedAchievements = unlockedAchievements,
                        greatResetMultiplier = profile?.greatResetMultiplier ?: 1.0,
                        passiveDustRatePerMin = passiveDustRate,
                        onBuyFossilUpgrade = { viewModel.buyFossilUpgrade(it) },
                        onBackToJar = { viewModel.setScreen(ActiveScreen.JAR_VIEW) }
                    )
                }

                ActiveScreen.KITCHEN_COUNTER -> {
                    KitchenCounterScreen(
                        activeJarId = activeJarId,
                        allJars = allJars,
                        fossilDust = fossilDust,
                        onSelectJar = { viewModel.selectJar(it) },
                        onUnlockJar = { viewModel.unlockJar(it) },
                        onBackToJar = { viewModel.setScreen(ActiveScreen.JAR_VIEW) }
                    )
                }

                ActiveScreen.SETTINGS -> {
                    SettingsScreen(
                        profile = profile,
                        onUpdateSettings = { sound, music, shake, notifs ->
                            viewModel.updateSettings(sound, music, shake, notifs)
                        },
                        onResetGame = { viewModel.resetAllGameProgress() },
                        onBackToJar = { viewModel.setScreen(ActiveScreen.JAR_VIEW) }
                    )
                }
            }

            // Dialogs
            activeEvent?.let { event ->
                NarrativeEventDialog(
                    event = event,
                    onChoiceSelected = { viewModel.resolveNarrativeChoice(it) },
                    onDismiss = { viewModel.dismissEvent() }
                )
            }

            extinctionState?.let { state ->
                ExtinctionDialog(
                    extinctionState = state,
                    onContinue = { viewModel.dismissExtinctionAndRebirth() }
                )
            }

            offlineEarnings?.let { result ->
                OfflineEarningsDialog(
                    result = result,
                    onClaim = { viewModel.dismissOfflineEarnings() }
                )
            }

            if (showCollapseConfirm) {
                CollapseConfirmDialog(
                    estimatedFossilDust = viewModel.calculateEstimatedCollapseDust(),
                    greatResetBonusPercent = viewModel.calculateEstimatedGreatResetBonus(),
                    currentMultiplier = profile?.greatResetMultiplier ?: 1.0,
                    onConfirm = { viewModel.executeGreatResetExtinction() },
                    onDismiss = { viewModel.dismissCollapseConfirm() }
                )
            }

            if (showJarSelectionMenu) {
                JarSelectionMenuDialog(
                    activeJarId = activeJarId,
                    allJars = allJars,
                    fossilDust = fossilDust,
                    onSelectJar = { viewModel.selectJar(it) },
                    onUnlockJar = { viewModel.unlockJar(it) },
                    onOpenKitchenCounter = { viewModel.setScreen(ActiveScreen.KITCHEN_COUNTER) },
                    onDismiss = { viewModel.dismissJarSelectionMenu() }
                )
            }

            // Top Floating Achievement Toast
            AchievementNotificationToast(
                achievement = achievementToast,
                onDismiss = { viewModel.dismissAchievementToast() }
            )
        }
    }
}
