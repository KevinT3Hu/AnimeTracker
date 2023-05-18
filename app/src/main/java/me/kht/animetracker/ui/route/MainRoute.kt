package me.kht.animetracker.ui.route

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import me.kht.animetracker.BuildConfig
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.NavigationRoute
import me.kht.animetracker.R
import me.kht.animetracker.ui.component.AirScheduleRoute
import me.kht.animetracker.ui.component.HomeRoute
import me.kht.animetracker.ui.component.WatchListRoute
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(viewModel: MainViewModel, rootNavController: NavController, finish: () -> Unit) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showNewWatchListDialog by remember { mutableStateOf(false) }
    var showDeleteWatchListDialog by remember { mutableStateOf(false) }

    val allWatchList = viewModel.allWatchList.collectAsState(initial = emptyList())

    val routeNavController = rememberNavController()
    val currentRoute = routeNavController.currentBackStackEntryAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        BackHandler(
            onBack = {
                if (drawerState.isOpen) {
                    scope.launch { drawerState.close() }
                } else {
                    finish.invoke()
                }
            }
        )

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {

                    val itemModifier = Modifier.fillMaxWidth().padding(Dimension.navigationDrawerItemHorizontalPadding)

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
                        modifier = itemModifier,
                        selected = currentRoute.value?.destination?.route == NavigationRoute.HOME_ROUTE,
                        onClick = {
                            routeNavController.navigateTo(NavigationRoute.HOME_ROUTE)
                            scope.launch {
                                drawerState.close()
                            }
                        })

                    NavigationDrawerItem(
                        label = { Text(text = stringResource(id = R.string.air_schedule)) },
                        selected = currentRoute.value?.destination?.route == NavigationRoute.SCHEDULE_ROUTE,
                        modifier = itemModifier,
                        onClick = {
                            routeNavController.navigateTo(NavigationRoute.SCHEDULE_ROUTE)
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
                            modifier = itemModifier,
                            label = { Text(text = it.watchList.title) },
                            selected = currentRoute.value?.destination?.route == NavigationRoute.WATCHLIST_ROUTE && viewModel.watchListTitle == it.watchList.title,
                            onClick = {
                                routeNavController.navigateTo(NavigationRoute.WATCHLIST_ROUTE)
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
                        onClick = { showNewWatchListDialog = true },
                        modifier = itemModifier
                    )
                    NavigationDrawerItem(
                        label = { Text(stringResource(id = R.string.about_title)) },
                        selected = false,
                        onClick = { rootNavController.navigate(NavigationRoute.ABOUT_ROUTE) },
                        modifier = itemModifier
                    )
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
                                text = when (currentRoute.value?.destination?.route) {
                                    "home" -> stringResource(R.string.app_name)
                                    "schedule" -> stringResource(R.string.air_schedule)
                                    else -> viewModel.watchListTitle
                                }
                            )
                        },
                        actions = {
                            if (currentRoute.value?.destination?.route == NavigationRoute.WATCHLIST_ROUTE) {
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
                    if (currentRoute.value?.destination?.route == "watchlist") {
                        FloatingActionButton(onClick = {
                            rootNavController.navigate("search")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                        }
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = routeNavController,
                    startDestination = NavigationRoute.HOME_ROUTE,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(NavigationRoute.HOME_ROUTE) {
                        HomeRoute(viewModel = viewModel)
                    }
                    composable(NavigationRoute.SCHEDULE_ROUTE) {
                        AirScheduleRoute(viewModel = viewModel)
                    }
                    composable(NavigationRoute.WATCHLIST_ROUTE) {
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

private fun NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(route) {
            inclusive = true
        }
        launchSingleTop = true
    }
}