package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import com.example.ui.components.EraPrestigeVisualizer
import com.example.ui.theme.CivilizationInAJarTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Civilization in a Jar", appName)
  }

  @Test
  fun `era prestige visualizer displays era and prestige`() {
    composeTestRule.setContent {
      CivilizationInAJarTheme {
        EraPrestigeVisualizer(
          eraIndex = 3,
          population = 75000.0,
          fossilDust = 1250.0,
          inGameYears = 120.0,
          globalPrestigeMultiplier = 2.5
        )
      }
    }

    composeTestRule.onNodeWithTag("era_prestige_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("current_era_title").assertIsDisplayed()
    composeTestRule.onNodeWithTag("fossil_dust_prestige_display").assertIsDisplayed()
  }
}

