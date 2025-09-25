package com.shahidshaadi.matchmate.ui.state

import com.shahidshaadi.matchmate.model.MatchProfile

sealed class MainUiState {
    data object Loading : MainUiState()
    data class Success(val matches: List<MatchProfile>) : MainUiState()
    data class Error(val message: String) : MainUiState()
}