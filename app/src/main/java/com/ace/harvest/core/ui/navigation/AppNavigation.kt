package com.ace.harvest.core.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.ace.harvest.R
import com.ace.harvest.features.visits.ui.VisitsFeatureEntry

@Composable
fun AppNavigation(navController: NavHostController) {
    val features = listOf(
        VisitsFeatureEntry(route = AppDestinations.VISITS),
    )

    NavHost(
        navController = navController,
        startDestination = AppDestinations.VISITS
    ) {
        features.forEach { feature ->
            composable(
                route = feature.route,
                arguments = feature.arguments,
                deepLinks = feature.deepLinks
            ) { navBackStackEntry -> feature.Draw(navBackStackEntry) }
        }
    }
}

val NavController.currentRouteTitle: String
    @Composable
    get() {
        val navBackStackEntry by currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: AppDestinations.HOME
        return when (currentRoute) {
            AppDestinations.VISITS -> stringResource(id = R.string.visits_module_name)
            else -> stringResource(id = R.string.app_name)
        }
    }
