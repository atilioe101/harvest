package com.ace.harvest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ace.harvest.core.permissions.PermissionManager
import com.ace.harvest.core.ui.IScreenMode
import com.ace.harvest.core.ui.navigation.AppDestinations
import com.ace.harvest.core.ui.navigation.AppNavigation
import com.ace.harvest.core.ui.navigation.currentRouteTitle
import com.ace.harvest.core.ui.theme.HarvestTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(PermissionManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.load()
        setContent {
            HarvestTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                when(state.mode){
                    is IScreenMode.Loading -> AppLoading()
                    is IScreenMode.Success -> {
                        val hasLocation by viewModel.hasLocationState
                        AppContent(hasLocation?: false)
                    }
                    is IScreenMode.Error -> TODO()
                    IScreenMode.Idle -> TODO()
                }
            }
        }
    }
}

@Composable
fun AppLoading() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFFFFF)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
             Image(
                modifier = Modifier.size(240.dp),
                painter = painterResource(id = R.drawable.harvest),
                contentDescription = "Harvest App Logo"
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF354E16),
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )
          /*  CircularProgressIndicator(
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(32.dp))*/

        }
    }
}

@Composable
fun AppContent(
    haslocation: Boolean
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { DrawerContent(navController, scope, drawerState) }
    ) {
        Scaffold(
            topBar = {
                TopBarContent(title = navController.currentRouteTitle, onMenuClick = {
                    scope.launch {
                        drawerState.open()
                    }
                })
            }
        ) { paddingValues ->
            Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                AppNavigation(navController, haslocation)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(navController: NavHostController, scope: CoroutineScope, drawerState: DrawerState) {
    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerContentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.visits_module_name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.clickable {
                    navController.navigate(AppDestinations.VISITS)
                    scope.launch { drawerState.close() }
                }
            )
        }
    }
}

@Composable
@OptIn( ExperimentalMaterial3Api::class)
fun TopBarContent(title: String, onMenuClick: () -> Unit) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
        }
    )
}
