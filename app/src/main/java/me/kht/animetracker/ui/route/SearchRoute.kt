package me.kht.animetracker.ui.route

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import me.kht.animetracker.MainViewModel
import me.kht.animetracker.R
import me.kht.animetracker.ui.component.animedetail.AnimeDetailDialog

@Composable
fun SearchRoute(viewModel: MainViewModel, rootNavController: NavController) {

    var showAnimeDetailDialog by remember { mutableStateOf(false) }
    var clickedAnimeItemId:Int? by remember { mutableStateOf(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        var keyword by rememberSaveable { mutableStateOf("") }
        val imageSize = 100.dp

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val scrollState = rememberLazyListState()

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                IconButton(onClick = { rootNavController.popBackStack() }, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
                OutlinedTextField(
                    value = keyword,
                    onValueChange = {
                        keyword = it
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchByKeyword(keyword, scrollState, scope, context)
                        }
                    ),
                    modifier = Modifier.weight(8f)
                )
                IconButton(onClick = {
                    viewModel.searchByKeyword(
                        keyword,
                        scrollState,
                        scope, context
                    )
                }, modifier = Modifier.weight(1f)) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider()

            if (viewModel.searching){
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center){
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = stringResource(R.string.searching))
                }
            }

            LazyColumn(state = scrollState) {
                items(viewModel.searchResult, key = {
                    it.id
                }) { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clickedAnimeItemId = item.id
                                showAnimeDetailDialog = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        AsyncImage(
                            model = item.image,
                            contentDescription = "",
                            modifier = Modifier.width(imageSize)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = item.nameCN,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = item.date,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                    Divider()
                }
            }
        }
    }

    if (showAnimeDetailDialog){
        if (clickedAnimeItemId == null){
            showAnimeDetailDialog=false
        }else{
            AnimeDetailDialog(animeId = clickedAnimeItemId!!, dismissDialog = { showAnimeDetailDialog=false }, viewModel = viewModel,showAdd = true)
        }
    }
}