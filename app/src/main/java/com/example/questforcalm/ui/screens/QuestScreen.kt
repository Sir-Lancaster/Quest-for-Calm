package com.example.questforcalm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun QuestScreen(userProgressDao: UserProgressDao) {
    val quests = listOf(
        "Meditate for 10 minutes",
        "Take a 30-minute walk",
        "Write in a journal"
    )

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier
        .padding(WindowInsets.safeDrawing.asPaddingValues())
        .padding(16.dp)) {
        Text("Self-Care Quests", style = MaterialTheme.typography.titleLarge)
        quests.forEach { quest ->
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val progress = userProgressDao.getUserProgress().first() ?: UserProgress()
                        val updatedProgress = progress.copy(
                            xp = progress.xp + 10,
                            level = calculateLevel(progress.xp + 10)
                        )
                        userProgressDao.insertUserProgress(updatedProgress)
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(quest)
            }
        }
    }
}

fun calculateLevel(xp: Int): Int = (xp / 100) + 1