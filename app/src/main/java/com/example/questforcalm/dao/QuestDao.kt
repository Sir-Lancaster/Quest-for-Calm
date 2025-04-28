package com.example.questforcalm.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.questforcalm.models.Quest
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuest(quest: Quest)

    @Query("SELECT * FROM quests WHERE isCompleted = 0")
    fun getActiveQuests(): Flow<List<Quest>>

    @Update
    suspend fun updateQuest(quest: Quest)
}