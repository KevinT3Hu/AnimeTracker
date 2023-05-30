package me.kht.animetracker.ui.route

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import me.kht.animetracker.BuildConfig
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutRoute(viewModel: MainViewModel, rootNavController: NavController, createExportDocLauncher: ActivityResultLauncher<String>, readImportDocLauncher: ActivityResultLauncher<Array<String>>) {

    val scrollState = rememberScrollState()

    val context = LocalContext.current

    var showExportImportDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.about_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            rootNavController.popBackStack()
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { innerPaddings ->

            val cardModifier = Modifier
                .padding(horizontal = 10.dp, vertical = 3.dp)
                .fillMaxWidth()

            val cardContentModifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(innerPaddings)
                .verticalScroll(scrollState)) {

                // version info
                Card(modifier = cardModifier.clickable {  }) {
                    Column(modifier = cardModifier, horizontalAlignment = Alignment.CenterHorizontally) {

                        val modifier = Modifier.padding(vertical = 5.dp)

                        Text(text = "Anime Tracker", style = MaterialTheme.typography.titleLarge,modifier=modifier)
                        Text(text = BuildConfig.VERSION_NAME, style = MaterialTheme.typography.titleMedium,modifier=modifier)
                        Text(text = "Built on ${BuildConfig.BUILD_TIME}", style = MaterialTheme.typography.titleMedium,modifier=modifier)
                        val textHeight = with(LocalDensity.current) { MaterialTheme.typography.titleMedium.lineHeight.toDp() }
                        Row(modifier=modifier) {
                           Text(text = "Project hosted on GitHub: ", style = MaterialTheme.typography.titleMedium)
                           Image(painter = painterResource(id = R.drawable.github_mark), contentDescription = "", modifier = Modifier
                               .height(textHeight)
                               .clickable {
                                   val intent = Intent(Intent.ACTION_VIEW).apply {
                                       data = Uri.parse(BuildConfig.PROJECT_URL)
                                   }
                                   intent
                                       .resolveActivity(context.packageManager)
                                       ?.let {
                                           context.startActivity(intent)
                                       }
                               })
                        }
                    }
                }

                // export/import
                Card(modifier = cardModifier, onClick = {
                    showExportImportDialog = true
                }) {
                    Column(
                        modifier = cardContentModifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.export_import),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // refresh database
                Card(modifier = cardModifier, onClick = {
                    viewModel.refreshDatabase(context)
                }) {
                    Row(
                        modifier = cardContentModifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val textHeight = with(LocalDensity.current) { MaterialTheme.typography.titleMedium.lineHeight.toDp() }
                        AnimatedVisibility(visible = viewModel.databaseRefreshing) {
                            CircularProgressIndicator(modifier = Modifier
                                .progressSemantics()
                                .size(textHeight))
                        }
                        Text(
                            text = if (viewModel.databaseRefreshing) stringResource(id = R.string.database_refreshing,viewModel.refreshingProgress,viewModel.refreshingTotal) else stringResource(id = R.string.refresh_database),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Third party licenses
                Card(modifier = cardModifier, onClick = {
                    context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
                }) {
                    Column(
                        modifier = cardContentModifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.third_party_licenses),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                // Test crash
                Card(modifier = cardModifier, onClick = {
                    throw RuntimeException("Test crash")
                }) {
                    Column(
                        modifier = cardContentModifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.test_crash),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showExportImportDialog){

        AlertDialog(onDismissRequest = { showExportImportDialog = false }){
            Surface(modifier = Modifier.padding(10.dp),shape = RoundedCornerShape(Dimension.alertDialogRoundedCorner)) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    FilledTonalButton(onClick = {
                        createExportDocLauncher.launch("export.json")
                    }, modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()) {
                        if (viewModel.databaseExporting){
                            CircularProgressIndicator()
                        }else{
                            Text(text = stringResource(id = R.string.export))
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    FilledTonalButton(onClick = {
                        readImportDocLauncher.launch(arrayOf("application/json"))
                    }, modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .fillMaxWidth()) {
                        if (viewModel.databaseImporting) {
                            CircularProgressIndicator()
                        } else {
                            Text(text = stringResource(id = R.string.import_))
                        }
                    }
                }
            }
        }
    }
}