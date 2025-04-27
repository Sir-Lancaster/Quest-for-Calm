package com.example.questforcalm.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.questforcalm.models.MoodLog

@Dao
interface MoodLogDao {
    @Insert
    suspend fun insertMoodLog(moodLog: MoodLog)

    @Query("SELECT * FROM mood_logs")
    abstract fun getAllMoodLogs(): List<MoodLog>

    @Delete
    suspend fun deleteMoodLog(moodLog: MoodLog)
}