package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    fun getPlayerProfile(): Flow<PlayerProfileEntity?>

    @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
    suspend fun getPlayerProfileDirect(): PlayerProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

    @Query("SELECT * FROM jar_states")
    fun getAllJarStates(): Flow<List<JarStateEntity>>

    @Query("SELECT * FROM jar_states")
    suspend fun getAllJarStatesDirect(): List<JarStateEntity>

    @Query("SELECT * FROM jar_states WHERE jarId = :jarId LIMIT 1")
    fun getJarState(jarId: String): Flow<JarStateEntity?>

    @Query("SELECT * FROM jar_states WHERE jarId = :jarId LIMIT 1")
    suspend fun getJarStateDirect(jarId: String): JarStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateJar(jar: JarStateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJarStates(jars: List<JarStateEntity>)

    @Query("SELECT * FROM fossil_upgrades")
    fun getAllFossilUpgrades(): Flow<List<FossilUpgradeEntity>>

    @Query("SELECT * FROM fossil_upgrades")
    suspend fun getAllFossilUpgradesDirect(): List<FossilUpgradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFossilUpgrade(upgrade: FossilUpgradeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFossilUpgrades(upgrades: List<FossilUpgradeEntity>)

    @Query("SELECT * FROM civilization_history ORDER BY timestamp DESC")
    fun getCivilizationHistory(): Flow<List<CivilizationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistoryEntry(entry: CivilizationHistoryEntity)

    @Query("SELECT * FROM unlocked_achievements")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM unlocked_achievements")
    suspend fun getUnlockedAchievementsDirect(): List<AchievementEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Query("DELETE FROM unlocked_achievements")
    suspend fun clearAchievements()

    @Query("DELETE FROM jar_states")
    suspend fun clearJarStates()

    @Query("DELETE FROM fossil_upgrades")
    suspend fun clearFossilUpgrades()

    @Query("DELETE FROM civilization_history")
    suspend fun clearHistory()

    @Query("DELETE FROM player_profile")
    suspend fun clearProfile()
}

@Database(
    entities = [
        PlayerProfileEntity::class,
        JarStateEntity::class,
        FossilUpgradeEntity::class,
        CivilizationHistoryEntity::class,
        AchievementEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}
