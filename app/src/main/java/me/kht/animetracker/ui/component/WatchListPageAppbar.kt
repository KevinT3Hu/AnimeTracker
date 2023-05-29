package me.kht.animetracker.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.NavigationRoute
import me.kht.animetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchListPageAppBar(viewModel: MainViewModel,drawerState:DrawerState,routeNavController:NavController){

    val scope = rememberCoroutineScope()

    val allWatchList = viewModel.allWatchList.collectAsState(initial = emptyList())

    TopAppBar(
        title = { Text(text = viewModel.watchListTitle) },
        navigationIcon = { IconButton(onClick = { scope.launch { drawerState.open() } }) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null
            )
        } },
        actions = {
            // delete
            IconButton(onClick = { viewModel.toggleShowDeleteWatchListDialog(true) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }

            val watchList = allWatchList.value.find { it.watchList.title == viewModel.watchListTitle }
            // archive
            if (watchList?.watchList?.archived == true){
                IconButton(onClick = {
                    viewModel.unarchiveWatchList(watchList.watchList.title)
                }) {
                    Icon(painter = painterResource(id = R.drawable.unarchive), contentDescription = "")
                }
            }else{
                IconButton(onClick = {
                    viewModel.archiveWatchList()
                    routeNavController.navigate(NavigationRoute.HOME_ROUTE){
                        popUpTo(NavigationRoute.HOME_ROUTE){
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
}