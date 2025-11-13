package com.ace.harvest.core.ui

data class UiSate(
    val mode: IScreenMode = IScreenMode.Idle,
    val items: List<String> = emptyList<String>()
)
