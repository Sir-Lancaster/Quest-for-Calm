package com.example.questforcalm.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun QuestScreen(userProgressDao: UserProgressDao) {
    val quests = listOf(
        "Meditate for 10 minutes",
        "Take a 30-minute walk",
        "Write in a journal"
    )

    val coroutineScope = rememberCoroutineScope()
    val userProgress = remember { mutableStateOf(UserProgress()) }
    val showLevelUpDialog = remember { mutableStateOf(false) }

    // Load user progress when the screen is launched
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val progress = userProgressDao.getUserProgress().firstOrNull() ?: UserProgress()
            userProgress.value = progress
        }
    }

    Column(
        modifier = Modifier
            .padding(WindowInsets.safeDrawing.asPaddingValues())
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text("Self-Care Quests", style = MaterialTheme.typography.titleLarge)
        quests.forEach { quest ->
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val progress = userProgressDao.getUserProgress().first() ?: UserProgress()
                        val newXp = progress.xp + 10
                        val newLevel = calculateLevel(newXp)
                        val updatedProgress = progress.copy(xp = newXp, level = newLevel)
                        userProgressDao.insertUserProgress(updatedProgress)
                        userProgress.value = updatedProgress

                        // Show the level-up dialog if the level has increased
                        if (newLevel > progress.level) {
                            showLevelUpDialog.value = true
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(quest)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push the progress bar to the bottom

        // Level progress bar
        val progressPercentage = (userProgress.value.xp % 100) / 100f
        Text(
            text = "Level: ${userProgress.value.level} (${(progressPercentage * 100).toInt()}%)",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = { progressPercentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        )
    }

    // Level-up dialog
    if (showLevelUpDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLevelUpDialog.value = false },
            title = { Text("Congratulations!") },
            text = { Text("So that's how it works. You plod along, putting one foot before the other, look up, and suddenly, there you are. Right where you wanted to be all along.") },
            confirmButton = {
                Button(onClick = { showLevelUpDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

fun calculateLevel(xp: Int): Int = (xp / 100) + 1