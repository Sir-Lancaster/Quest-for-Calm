package com.example.questforcalm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.questforcalm.dao.MoodLogDao
import com.example.questforcalm.models.MoodLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MoodHistoryScreen(moodLogDao: MoodLogDao) {
    val coroutineScope = rememberCoroutineScope()
    val moodLogs = remember { mutableStateOf<List<MoodLog>>(emptyList()) }

    // Load mood logs when the screen is launched
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val logs = moodLogDao.getAllMoodLogs()
            moodLogs.value = logs
        }
    }

    Column(
        modifier = Modifier
            .padding(WindowInsets.safeDrawing.asPaddingValues()) // Respect system insets
            .padding(16.dp) // Additional padding for content
    ) {
        Text("Mood History", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        moodLogs.value.forEach { moodLog ->
            Column {
                Text("${moodLog.date}: ${moodLog.moodScore}/10 - ${moodLog.description}")
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            moodLogDao.deleteMoodLog(moodLog)
                            // Refresh the list after deletion
                            val updatedLogs = moodLogDao.getAllMoodLogs()
                            moodLogs.value = updatedLogs
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("Delete")
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}