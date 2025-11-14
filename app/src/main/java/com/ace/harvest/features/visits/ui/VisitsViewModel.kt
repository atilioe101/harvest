package com.ace.harvest.features.visits.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ace.harvest.features.visits.domain.model.Area
import com.ace.harvest.features.visits.domain.usecase.GetAreasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class VisitsViewModel @Inject constructor(
    private val getAreasUseCase: GetAreasUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<VisitsUiState> =
        MutableStateFlow(VisitsUiState.Loading)
    val uiState: StateFlow<VisitsUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = VisitsUiState.Loading
        viewModelScope.launch {
            runCatching { getAreasUseCase.execute(Unit) }
                .onSuccess { areas ->
                    _uiState.value = VisitsUiState.Success(areas)
                }
                .onFailure { throwable ->
                    _uiState.value = VisitsUiState.Error(throwable.localizedMessage ?: "Unknown error")
                }
        }
    }
}

sealed interface VisitsUiState {
    data object Loading : VisitsUiState
    data class Success(val areas: List<Area>) : VisitsUiState
    data class Error(val message: String) : VisitsUiState
}
