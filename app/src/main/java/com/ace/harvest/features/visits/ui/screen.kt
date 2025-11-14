package com.ace.harvest.features.visits.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Ballot
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ace.harvest.R
import com.ace.harvest.features.visits.ui.VisitsUiState
import com.ace.harvest.features.visits.ui.VisitsViewModel
import com.ace.harvest.features.visits.ui.screens.ListScreen
import com.ace.harvest.features.visits.ui.screens.MapScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

sealed class VisitsNavigation(val route: String, @StringRes val label: Int, val icon: ImageVector) {
    object Map : VisitsNavigation("map", R.string.visits_map, Icons.Default.LocationOn)
    object List : VisitsNavigation("list", R.string.visits_list, Icons.Default.Ballot)
}

@Composable
fun VisitsScreen(
    hasLocation: Boolean,
    viewModel: VisitsViewModel = hiltViewModel()
) {
    var selectedScreen by remember { mutableStateOf<VisitsNavigation>(VisitsNavigation.Map) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            NavigationBarContent(
                selectedScreen = selectedScreen,
                onScreenSelected = { screen ->
                    selectedScreen = screen
                }
            )
        }
    ) { innerPadding ->
        val areas = (uiState as? VisitsUiState.Success)?.areas.orEmpty()
        val isLoading = uiState is VisitsUiState.Loading
        val errorMessage = (uiState as? VisitsUiState.Error)?.message

        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val alphaMap: Float
            val alphaList: Float

            when (selectedScreen) {
                VisitsNavigation.Map -> {alphaMap = 1f; alphaList = 0f}
                else -> {alphaMap = 0f; alphaList = 1f}
            }
            Box(modifier = Modifier.graphicsLayer { alpha = alphaMap }) {
                MapScreen(hasLocation)
            }
            Box(modifier = Modifier.graphicsLayer { alpha = alphaList }) {
                ListScreen(
                    areas = areas,
                    isLoading = isLoading,
                    errorMessage = errorMessage,
                    onRetry = viewModel::refresh
                )
            }

            when {
                isLoading -> {
                    LoadingState()
                }
                errorMessage != null -> {
                    ErrorState(message = errorMessage, onRetry = viewModel::refresh)
                }
            }
        }
    }
}

@Composable
fun NavigationBarContent(
    selectedScreen: VisitsNavigation,
    onScreenSelected: (VisitsNavigation) -> Unit
) {
    val screens = listOf(VisitsNavigation.Map, VisitsNavigation.List)
    NavigationBar {
        screens.forEach { screen ->
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
private fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(text = message, textAlign = TextAlign.Center)
            Button(onClick = onRetry) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}