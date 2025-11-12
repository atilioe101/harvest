package com.ace.harvest.features.visits.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import com.ace.harvest.core.ui.navigation.UiFeatureEntry
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.ace.harvest.R
import com.ace.harvest.features.visits.ui.screens.ListScreen
import com.ace.harvest.features.visits.ui.screens.MapScreen

class VisitsFeatureEntry(override val route: String) : UiFeatureEntry {
    @Composable
    override fun Draw(navBackStackEntry: NavBackStackEntry) {
        MainScreen()
    }
}

sealed class VisitsNavigation(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    object Map : VisitsNavigation("map", R.string.visits_map, Icons.Default.LocationOn)
    object List : VisitsNavigation("list", R.string.visits_list, Icons.Default.Ballot)
}

@Composable
fun MainScreen() {
    var selectedScreen by remember { mutableStateOf<VisitsNavigation>(VisitsNavigation.Map) }

    Scaffold(
        bottomBar = {
            NavigationBarContent(
                selectedScreen = selectedScreen,
                onScreenSelected = { screen -> selectedScreen = screen }
            )
        }
    ) { innerPadding ->
        MainContent(innerPadding, selectedScreen)
    }
}

@Composable
fun NavigationBarContent(
    selectedScreen: VisitsNavigation,
    onScreenSelected: (VisitsNavigation) -> Unit
) {
    NavigationBar {
        listOf(
            VisitsNavigation.Map,
            VisitsNavigation.List
        ).forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = stringResource(screen.label)) },
                label = { Text(stringResource(screen.label)) },
                selected = selectedScreen == screen,
                onClick = { onScreenSelected(screen) }
            )
        }
    }
}

@Composable
fun MainContent(innerPadding: PaddingValues, selectedScreen: VisitsNavigation){
    Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedScreen) {
                VisitsNavigation.Map -> MapScreen()
                VisitsNavigation.List -> ListScreen()
            }
    }
}