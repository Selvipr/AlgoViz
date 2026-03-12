package com.example.algoviz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algoviz.domain.repository.AppTheme
import com.example.algoviz.domain.repository.SettingsRepository
import com.example.algoviz.ui.MainScreen
import com.example.algoviz.ui.theme.AlgoVizTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by settingsRepository.theme.collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)
            
            val isDarkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            AlgoVizTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}