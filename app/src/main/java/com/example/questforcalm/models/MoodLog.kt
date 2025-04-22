package com.example.questforcalm.models
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_logs")
data class MoodLog (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String, // Date in YYYY-MM-DD format
    val moodScore: Int,
    val description: String
)