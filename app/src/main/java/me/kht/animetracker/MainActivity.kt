package me.kht.animetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.kht.animetracker.ui.route.AboutRoute
import me.kht.animetracker.ui.route.MainRoute
import me.kht.animetracker.ui.route.SearchRoute
import me.kht.animetracker.ui.theme.AnimeTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: MainViewModel by viewModels()

        val createExportDoc = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")){ uri ->
            uri?.let {
                viewModel.exportDatabase(this,it)
            }
        }

        val readImportDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri ->
            uri?.let {
                viewModel.importDatabase(this,it)
            }
        }

        setContent {

            val rootNavController = rememberNavController()

            AnimeTrackerTheme {
                NavHost(
                    navController = rootNavController,
                    startDestination = NavigationRoute.MAIN_ROUTE
                ) {
                    composable(NavigationRoute.MAIN_ROUTE) {
                        MainRoute(
                            viewModel = viewModel,
                            rootNavController = rootNavController,
                            finish = {
                                finish()
                            })
                    }
                    composable(NavigationRoute.SEARCH_ROUTE) {
                        SearchRoute(
                            viewModel = viewModel,
                            rootNavController = rootNavController)
                    }
                    composable(NavigationRoute.ABOUT_ROUTE){
                        AboutRoute(viewModel = viewModel, rootNavController= rootNavController, createExportDocLauncher = createExportDoc, readImportDocLauncher = readImportDoc)
                    }
                }
            }
        }
    }
}