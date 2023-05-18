package me.kht.animetracker.ui.route

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.ui.theme.Dimension

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutRoute(viewModel: MainViewModel, rootNavController: NavController, createExportDocLauncher: ActivityResultLauncher<String>, readImportDocLauncher: ActivityResultLauncher<Array<String>>){

    val scrollState = rememberScrollState()

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
                .padding(10.dp)
                .fillMaxWidth()

            val cardContentModifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(innerPaddings)
                .verticalScroll(scrollState)) {

                Card(modifier = cardModifier, onClick = {
                    showExportImportDialog = true
                }) {
                    Column(modifier = cardContentModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.export_import), style = MaterialTheme.typography.titleMedium)
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
                        if (viewModel.databaseExporting.value){
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
                        if (viewModel.databaseImporting.value) {
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