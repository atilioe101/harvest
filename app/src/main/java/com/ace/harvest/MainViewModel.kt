package com.ace.harvest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ace.harvest.core.permissions.PermissionManager
import com.ace.harvest.core.ui.IScreenMode
import com.ace.harvest.core.ui.UiSate
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(private val permissionManager: PermissionManager) : ViewModel() {
    private val _hasLocationState = mutableStateOf<Boolean?>(null)
    private val _uiSate = MutableStateFlow(UiSate())

    val hasLocationState: State<Boolean?> = _hasLocationState
    val uiState: StateFlow<UiSate> = _uiSate

    class MainViewModelFactory(private val permissionManager: PermissionManager) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(permissionManager) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    fun load() = viewModelScope.launch {
        if (_uiSate.value.mode == IScreenMode.Idle) {

            _uiSate.update { it.copy(mode = IScreenMode.Loading) }
            if (permissionManager.requestLocationPermission()) {
                _hasLocationState.value = permissionManager.requestLocationSettings()
            } else {
                _hasLocationState.value = false
            }
            delay(5000)
            _uiSate.update { it.copy(mode = IScreenMode.Success)}
        }
    }
}
