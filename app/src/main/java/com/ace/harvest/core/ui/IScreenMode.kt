package com.ace.harvest.core.ui

sealed interface IScreenMode {
    data object Idle: IScreenMode
    data object Loading: IScreenMode
    data class Error(val message: String): IScreenMode
    data object Success: IScreenMode
}


