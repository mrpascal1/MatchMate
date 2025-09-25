package com.shahidshaadi.matchmate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shahidshaadi.matchmate.data.repository.MatchRepository
import com.shahidshaadi.matchmate.model.MatchProfile
import com.shahidshaadi.matchmate.ui.state.MainUiState
import com.shahidshaadi.matchmate.utils.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MatchRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    val networkState: StateFlow<Boolean?> = networkMonitor.isConnected

    private var initialDataFetched = false

    init {
        loadCachedData()
        initializeAndLoadData()
    }

    private fun loadCachedData() {
        viewModelScope.launch {
            repository.allMatches.collect { matches ->
                _uiState.value = MainUiState.Success(matches)
            }
        }
    }

    private fun initializeAndLoadData() {
        viewModelScope.launch {
            repository.initializePagination()

            networkState.collect { isConnected ->
                if (!initialDataFetched && isConnected == true) {
                    initialDataFetched = true
                    _isLoadingMore.value = true
                    repository.loadInitialPage()
                    _isLoadingMore.value = false
                }
            }
        }
    }

    fun onAccept(id: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(id, "accepted")
        }
    }

    fun onDecline(id: String) {
        viewModelScope.launch {
            repository.updateMatchStatus(id, "declined")
        }
    }

    fun loadMore() {
        if (networkState.value == true && !_isLoadingMore.value) {
            _isLoadingMore.value = true
            viewModelScope.launch {
                repository.loadNextPage()
                _isLoadingMore.value = false
            }
        }
    }
}
