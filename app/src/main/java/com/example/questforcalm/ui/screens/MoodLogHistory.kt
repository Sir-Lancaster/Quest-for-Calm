package com.example.questforcalm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val logs = moodLogDao.getAllMoodLogs()
            moodLogs.value = logs
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Mood History", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        moodLogs.value.forEach { moodLog ->
            Text("${moodLog.date}: ${moodLog.moodScore}/10 - ${moodLog.description}")
            Spacer(Modifier.height(8.dp))
        }
    }
}
