package com.example.algoviz.ui.screens.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algoviz.domain.repository.BrowserHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor(
    private val browserHistoryRepository: BrowserHistoryRepository
) : ViewModel() {

    fun recordHistory(url: String) {
        viewModelScope.launch {
            browserHistoryRepository.recordHistory(url)
        }
    }
}
