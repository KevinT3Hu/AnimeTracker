package me.kht.animetracker.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.NavigationRoute
import me.kht.animetracker.R

@OptIn(ExperimentalMaterial3Api::class,ExperimentalAnimationApi::class)
@Composable
fun WatchListPageAppBar(viewModel: MainViewModel,drawerState:DrawerState,routeNavController:NavController) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val allWatchList = viewModel.allWatchList.collectAsState(initial = emptyList())

    LaunchedEffect(key1 = viewModel.selectedAnimeStates.size) {
        val actionMode = viewModel.selectedAnimeStates.size > 0
        viewModel.toggleActionMode(actionMode)
    }

    AnimatedContent(targetState = viewModel.actionMode) { actionModeOn ->
        if (!actionModeOn) {
            TopAppBar(
                title = { Text(text = viewModel.watchListTitle) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    // delete
                    IconButton(onClick = { viewModel.toggleShowDeleteWatchListDialog(true) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }

                    val watchList =
                        allWatchList.value.find { it.watchList.title == viewModel.watchListTitle }
                    // archive
                    if (watchList?.watchList?.archived == true) {
                        IconButton(onClick = {
                            viewModel.unarchiveWatchList(watchList.watchList.title)
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.unarchive),
                                contentDescription = ""
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            viewModel.archiveWatchList()
                            routeNavController.navigate(NavigationRoute.HOME_ROUTE) {
                                popUpTo(NavigationRoute.HOME_ROUTE) {
                                    inclusive = true
                                }
                            }
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.archive),
                                contentDescription = null
                            )
                        }
                    }
                }
            )
        } else {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.items_selected,viewModel.selectedAnimeStates.size)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                navigationIcon = {
                    IconButton(onClick = { viewModel.toggleActionMode(false) }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {

                    // Refresh
                    IconButton(onClick = { viewModel.refreshDatabase(context,viewModel.watchListTitle) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                    }

                    // Delete
                    IconButton(onClick = { viewModel.removeSelectedItemsFromWatchList() }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null
                        )
                    }

                    // Hide
                    IconButton(onClick = { viewModel.hideSelectedItems() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.visibility_off),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}