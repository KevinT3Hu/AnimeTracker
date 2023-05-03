package me.kht.animetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.kht.animetracker.ui.component.AirScheduleRoute
import me.kht.animetracker.ui.component.HomeRoute
import me.kht.animetracker.ui.component.WatchListRoute
import me.kht.animetracker.ui.theme.AnimeTrackerTheme
import me.kht.animetracker.ui.theme.Dimension

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MainViewModel by viewModels()
        setContent {

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            var showAddDialog by remember { mutableStateOf(false) }
            var showNewWatchListDialog by remember { mutableStateOf(false) }
            var showDeleteWatchListDialog by remember { mutableStateOf(false) }

            val allWatchList = viewModel.allWatchList.collectAsState(initial = emptyList())

            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState()
            val isHomeRoute by remember {
                derivedStateOf {
                    currentRoute.value?.destination?.route == "home"
                }
            }
            val isAirScheduleRoute by remember {
                derivedStateOf {
                    currentRoute.value?.destination?.route == "schedule"
                }
            }

            AnimeTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    BackHandler(
                        onBack = {
                            if (drawerState.isOpen) {
                                scope.launch { drawerState.close() }
                            } else {
                                finish()
                            }
                        }
                    )

                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(Dimension.navigationDrawerHeaderHeight)
                                        .background(
                                            color = MaterialTheme.colorScheme.secondaryContainer.copy(
                                                alpha = 0.5f
                                            )
                                        )
                                ) {
                                    Text(
                                        text = "Anime Tracker",
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .align(Alignment.Center),
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    // version
                                    Text(
                                        text = BuildConfig.VERSION_NAME,
                                        modifier = Modifier
                                            .padding(16.dp)
                                            .fillMaxWidth()
                                            .align(Alignment.BottomStart),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                NavigationDrawerItem(
                                    label = { Text(text = stringResource(R.string.home)) },
                                    modifier = Modifier.padding(Dimension.navigationDrawerItemHorizontalPadding),
                                    selected = isHomeRoute,
                                    onClick = {
                                        navController.navigateTo("home")
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    })

                                NavigationDrawerItem(
                                    label = { Text(text = stringResource(id = R.string.air_schedule)) },
                                    selected = isAirScheduleRoute,
                                    modifier = Modifier.padding(Dimension.navigationDrawerItemHorizontalPadding),
                                    onClick = {
                                        navController.navigateTo("schedule")
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    })

                                // label
                                Text(
                                    text = stringResource(R.string.watch_lists),
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.labelSmall
                                )

                                allWatchList.value.forEach {
                                    NavigationDrawerItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(Dimension.navigationDrawerItemHorizontalPadding),
                                        label = { Text(text = it.watchList.title) },
                                        selected = !(isHomeRoute||isAirScheduleRoute) && viewModel.watchListTitle == it.watchList.title,
                                        onClick = {
                                            navController.navigateTo("watchlist")
                                            viewModel.watchListTitle = it.watchList.title
                                            scope.launch {
                                                drawerState.close()
                                            }
                                        })
                                }

                                Divider(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    )
                                )

                                NavigationDrawerItem(
                                    label = { Text(stringResource(R.string.create_new_watch_list)) },
                                    selected = false,
                                    onClick = { showNewWatchListDialog = true })
                            }
                        }
                    ) {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    navigationIcon = {
                                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    title = {
                                        Text(
                                            text = when {
                                                isHomeRoute -> stringResource(R.string.app_name)
                                                isAirScheduleRoute -> stringResource(R.string.air_schedule)
                                                else -> viewModel.watchListTitle
                                            }
                                        )
                                    },
                                    actions = {
                                        if (!isHomeRoute && !isAirScheduleRoute) {
                                            IconButton(onClick = {
                                                showDeleteWatchListDialog = true
                                            }) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                    }
                                )
                            },
                            floatingActionButton = {
                                if (!isHomeRoute && !isAirScheduleRoute) {
                                    FloatingActionButton(onClick = { showAddDialog = true }) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                        ) { paddingValues ->
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                                modifier = Modifier.padding(paddingValues)
                            ) {
                                composable("home") {
                                    HomeRoute(viewModel = viewModel)
                                }
                                composable("schedule") {
                                    AirScheduleRoute(viewModel = viewModel)
                                }
                                composable("watchlist") {
                                    WatchListRoute(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }

                if (showNewWatchListDialog) {

                    var title by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showNewWatchListDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.newWatchList(title)
                                scope.launch {
                                    drawerState.close()
                                }
                                showNewWatchListDialog = false
                            }, enabled = title.isNotBlank()) {
                                Text(text = stringResource(R.string.confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showNewWatchListDialog = false }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(R.string.create_a_new_watch_list)) },
                        text = { TextField(value = title, onValueChange = { title = it }) }
                    )
                }

                if (showAddDialog) {

                    var id by remember { mutableStateOf("") }

                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.addItemToWatchList(
                                    viewModel.watchListTitle,
                                    id.toInt()
                                )
                                navController.navigateTo("watchlist")
                                showAddDialog = false
                            }, enabled = id.isNotBlank()) {
                                Text(text = stringResource(id = R.string.confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(R.string.add_a_new_anime)) },
                        text = {
                            TextField(
                                value = id,
                                onValueChange = { id = it },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                            )
                        }
                    )
                }

                if (showDeleteWatchListDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteWatchListDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.deleteWatchList(allWatchList.value.find { it.watchList.title != viewModel.watchListTitle }?.watchList?.title)
                                showDeleteWatchListDialog = false
                            }) {
                                Text(text = stringResource(id = R.string.confirm))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteWatchListDialog = false }) {
                                Text(text = stringResource(id = R.string.cancel))
                            }
                        },
                        title = { Text(text = stringResource(R.string.delete_watch_list)) },
                        text = { Text(text = stringResource(R.string.delete_watch_list_confirm)) }
                    )
                }
            }
        }
    }

    private fun NavController.navigateTo(route: String) {
        navigate(route) {
            popUpTo(route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
}