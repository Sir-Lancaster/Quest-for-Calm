package com.example.questforcalm.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.questforcalm.models.MoodLog

@Dao
interface MoodLogDao {
    @Insert
    suspend fun insertMoodLog(moodLog: MoodLog)

    @Query("SELECT * FROM mood_logs ORDER BY date DESC")
    fun getAllMoodLogs(): List<MoodLog>
}