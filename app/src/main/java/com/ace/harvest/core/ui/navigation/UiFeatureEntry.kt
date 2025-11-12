package com.ace.harvest.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink

interface UiFeatureEntry {
    val route: String
    val arguments: List<NamedNavArgument> get() = emptyList()
    val deepLinks: List<NavDeepLink> get() = emptyList()
    @Composable
    fun Draw(navBackStackEntry: NavBackStackEntry)
}