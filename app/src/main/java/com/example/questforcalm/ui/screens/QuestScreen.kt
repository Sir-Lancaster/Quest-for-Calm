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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.questforcalm.dao.QuestDao
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.Quest
import com.example.questforcalm.models.UserProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@Composable
fun QuestScreen(userProgressDao: UserProgressDao, questDao: QuestDao) {
    val coroutineScope = rememberCoroutineScope()
    val quests = remember { mutableStateOf<List<Quest>>(emptyList()) }
    val userProgress = remember { mutableStateOf(UserProgress()) }
    val showLevelUpDialog = remember { mutableStateOf(false) }
    val showAddQuestDialog = remember { mutableStateOf(false) }
    val newQuestTitle = remember { mutableStateOf("") }
    val newQuestDescription = remember { mutableStateOf("") }
    val newQuestXpReward = remember { mutableStateOf(0) }

    // Load quests and user progress when the screen is launched
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            questDao.getActiveQuests().collect { activeQuests ->
                quests.value = activeQuests
            }
        }
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
        Text("Quests", style = MaterialTheme.typography.titleLarge)

        // Add Quest Button
        Button(
            onClick = { showAddQuestDialog.value = true },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("Add Quest")
        }

        // Display existing quests
        quests.value.forEach { quest ->
            Button(
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        val progress = userProgressDao.getUserProgress().first() ?: UserProgress()
                        val newXp = progress.xp + quest.xpReward
                        val newLevel = calculateLevel(newXp)
                        val updatedProgress = progress.copy(xp = newXp, level = newLevel)
                        userProgressDao.insertUserProgress(updatedProgress)
                        userProgress.value = updatedProgress

                        // Mark the quest as completed
                        questDao.updateQuest(quest.copy(isCompleted = true))

                        // Show the level-up dialog if the level has increased
                        if (newLevel > progress.level) {
                            showLevelUpDialog.value = true
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(quest.title)
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

    // Add Quest Dialog
    if (showAddQuestDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddQuestDialog.value = false },
            title = { Text("Add New Quest") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newQuestTitle.value,
                        onValueChange = { newQuestTitle.value = it },
                        label = { Text("Quest Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newQuestDescription.value,
                        onValueChange = { newQuestDescription.value = it },
                        label = { Text("Quest Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newQuestXpReward.value.toString(),
                        onValueChange = { newQuestXpReward.value = it.toIntOrNull() ?: 0 },
                        label = { Text("XP Reward") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val newQuest = Quest(
                                title = newQuestTitle.value,
                                description = newQuestDescription.value,
                                xpReward = newQuestXpReward.value
                            )
                            questDao.insertQuest(newQuest)
                        }
                        showAddQuestDialog.value = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showAddQuestDialog.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Level-up dialog
    if (showLevelUpDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLevelUpDialog.value = false },
            title = { Text("Congratulations!") },
            text = { Text("You leveled up!") },
            confirmButton = {
                Button(onClick = { showLevelUpDialog.value = false }) {
                    Text("OK")
                }
            }
        )
    }
}

fun calculateLevel(xp: Int): Int = (xp / 100) + 1