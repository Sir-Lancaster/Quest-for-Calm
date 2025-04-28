package com.example.questforcalm

import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.questforcalm.database.QFCDatabase
import com.example.questforcalm.dao.MoodLogDao
import com.example.questforcalm.dao.UserProgressDao
import com.example.questforcalm.models.MoodLog
import com.example.questforcalm.ui.screens.MoodHistoryScreen
import com.example.questforcalm.ui.screens.QuestScreen
import com.example.questforcalm.ui.theme.QuestForCalmTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: QFCDatabase
    private lateinit var moodLogDao: MoodLogDao
    private lateinit var userProgressDao: UserProgressDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `user_progress` (
                        `id` INTEGER NOT NULL PRIMARY KEY,
                        `xp` INTEGER NOT NULL,
                        `level` INTEGER NOT NULL
                    )
                    """
                )
            }
        }

        database = Room.databaseBuilder(
            applicationContext,
            QFCDatabase::class.java,
            "qfc_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

        moodLogDao = database.moodLogDao()
        userProgressDao = database.userProgressDao()

        setContent {
            QuestForCalmTheme {
                val navController = rememberNavController()
                AppNavigator(navController, moodLogDao, userProgressDao)
            }
        }
    }
}

@Composable
fun AppNavigator(navController: NavHostController, moodLogDao: MoodLogDao, userProgressDao: UserProgressDao) {
    NavHost(navController = navController, startDestination = "moodLogScreen") {
        composable("moodLogScreen") {
            MoodLogScreen(moodLogDao, navController)
        }
        composable("moodHistoryScreen") {
            MoodHistoryScreen(moodLogDao)
        }
        composable("questScreen") {
            QuestScreen(userProgressDao)
        }
    }
}

@Composable
fun MoodLogScreen(moodLogDao: MoodLogDao, navController: NavHostController) {
    val moodScore = remember { mutableStateOf(5) }
    val description = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(WindowInsets.safeDrawing.asPaddingValues()) // Respect system insets
            .padding(16.dp) // Additional padding for content
            .fillMaxSize()
    ) {
        Text(text = "How's your mood today?", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = moodScore.value.toFloat(),
            onValueChange = { moodScore.value = it.toInt() },
            valueRange = 1f..10f,
            steps = 8
        )
        Text("Mood Score: ${moodScore.value}")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description.value,
            onValueChange = { description.value = it },
            label = { Text("Write freely about your mood.") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    val currentDate = Calendar.getInstance().time
                    val moodLog = MoodLog(
                        date = currentDate.toString(),
                        moodScore = moodScore.value,
                        description = description.value
                    )
                    moodLogDao.insertMoodLog(moodLog)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Mood")
        }
        Button(
            onClick = {
                navController.navigate("moodHistoryScreen")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("View Mood History")
        }
        Button(
            onClick = {
                navController.navigate("questScreen")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("View Quests")
        }
    }
}