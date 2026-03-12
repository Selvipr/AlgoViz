package com.example.algoviz.domain.repository

import kotlinx.coroutines.flow.Flow

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}

interface SettingsRepository {
    val theme: Flow<AppTheme>
    
    suspend fun setTheme(theme: AppTheme)
}
