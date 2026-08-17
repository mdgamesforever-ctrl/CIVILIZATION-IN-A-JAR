package com.example

import com.example.data.Era
import com.example.data.ExtinctionType
import com.example.data.GameRepository
import com.example.data.JarType
import com.example.util.NumberFormatter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameLogicUnitTest {

    @Test
    fun testNumberFormatter() {
        assertEquals("0", NumberFormatter.format(0.0))
        assertEquals("450", NumberFormatter.format(450.0))
        assertEquals("1.20K", NumberFormatter.format(1200.0))
        assertEquals("5.00M", NumberFormatter.format(5000000.0))
        assertEquals("3.50B", NumberFormatter.format(3500000000.0))
        assertEquals("1.00T", NumberFormatter.format(1000000000000.0))
    }

    @Test
    fun testNumberFormatterYears() {
        assertEquals("0 yrs", NumberFormatter.formatYears(0.0))
        assertEquals("10 yrs", NumberFormatter.formatYears(10.0))
        assertEquals("2.50K yrs", NumberFormatter.formatYears(2500.0))
    }

    @Test
    fun testEraProgression() {
        assertEquals(9, Era.entries.size)
        assertEquals(1, Era.PRIMORDIAL_SOUP.index)
        assertEquals(2, Era.FIRST_LIFE.index)
        assertEquals(3, Era.TRIBAL_AGE.index)
        assertEquals(4, Era.AGRICULTURAL_AGE.index)
        assertEquals(5, Era.CITY_AGE.index)
        assertEquals(6, Era.INDUSTRIAL_AGE.index)
        assertEquals(7, Era.DIGITAL_AGE.index)
        assertEquals(8, Era.SPACE_AGE.index)
        assertEquals(9, Era.ASCENSION.index)

        // Verify required Fossil Dust increases strictly with each era
        for (i in 1..8) {
            val currentEra = Era.fromIndex(i)
            val nextEra = Era.fromIndex(i + 1)
            assertTrue(
                "Era ${nextEra.title} should require more Fossil Dust than ${currentEra.title}",
                nextEra.requiredFossilDust > currentEra.requiredFossilDust
            )
        }

        assertEquals(0.0, Era.PRIMORDIAL_SOUP.requiredFossilDust, 0.001)
        assertEquals(10.0, Era.FIRST_LIFE.requiredFossilDust, 0.001)
        assertEquals(50.0, Era.TRIBAL_AGE.requiredFossilDust, 0.001)
        assertEquals(200.0, Era.AGRICULTURAL_AGE.requiredFossilDust, 0.001)
        assertEquals(1000.0, Era.CITY_AGE.requiredFossilDust, 0.001)
        assertEquals(5000.0, Era.INDUSTRIAL_AGE.requiredFossilDust, 0.001)
        assertEquals(25000.0, Era.DIGITAL_AGE.requiredFossilDust, 0.001)
        assertEquals(100000.0, Era.SPACE_AGE.requiredFossilDust, 0.001)
        assertEquals(500000.0, Era.ASCENSION.requiredFossilDust, 0.001)
    }

    @Test
    fun testFossilDustCalculation() {
        val upgrades = mapOf("fd_sediment_sieve" to 2)
        val baseDust = GameRepository.calculateFossilDustEarned(
            peakPopulation = 100000.0,
            eraIndex = 5,
            extinctionType = ExtinctionType.PEACEFUL_ASCENSION,
            fossilUpgrades = upgrades,
            greatResetMultiplier = 1.0
        )
        assertTrue("Fossil dust must be >= 5", baseDust >= 5.0)

        val boostedDust = GameRepository.calculateFossilDustEarned(
            peakPopulation = 100000.0,
            eraIndex = 5,
            extinctionType = ExtinctionType.PEACEFUL_ASCENSION,
            fossilUpgrades = upgrades,
            greatResetMultiplier = 2.5
        )
        assertEquals(baseDust * 2.5, boostedDust, 1.0)
    }

    @Test
    fun testGreatResetBonusCalculation() {
        val ascensionBonus = GameRepository.calculateGreatResetMultiplierGain(
            eraIndex = 9,
            peakPopulation = 1000000.0,
            extinctionType = ExtinctionType.PEACEFUL_ASCENSION
        )
        assertTrue(ascensionBonus >= 1.0)

        val earlyEraBonus = GameRepository.calculateGreatResetMultiplierGain(
            eraIndex = 2,
            peakPopulation = 500.0,
            extinctionType = ExtinctionType.METEOR_IMPACT
        )
        assertTrue(earlyEraBonus > 0.0)
        assertTrue(ascensionBonus > earlyEraBonus)
    }

    @Test
    fun testJarMultipliers() {
        assertEquals(1.0, JarType.MASON.growthMultiplier, 0.001)
        assertEquals(2.5, JarType.SPICE.resourceMultiplier, 0.001)
        assertEquals(2.0, JarType.JAM.growthMultiplier, 0.001)
    }

    @Test
    fun testNarrativeEventCatalogForEras() {
        for (eraIndex in 1..9) {
            val events = com.example.data.NarrativeEventCatalog.getEventsForEra(eraIndex)
            assertTrue("Each era should have available events: era $eraIndex", events.isNotEmpty())
            val randomEvent = com.example.data.NarrativeEventCatalog.getRandomEventForEra(eraIndex)
            assertTrue(randomEvent != null)
            assertTrue(eraIndex >= randomEvent!!.minEra && eraIndex <= randomEvent.maxEra)
        }
    }
}
