package com.example.praktam_2407051018

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.praktam_2407051018.data.model.MonsterApiModel
import com.example.praktam_2407051018.ui.MonsterUiState
import com.example.praktam_2407051018.ui.MonsterViewModel
import com.example.praktam_2407051018.ui.Screen
import com.example.praktam_2407051018.ui.theme.PraktamTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PraktamTheme {
                // bikin navigator biar bisa pindah-pindah halaman
                val navController = rememberNavController()
                MainScreen(navController)
            }
        }
    }
}

@Composable
fun MainScreen(navController: androidx.navigation.NavHostController) {
    val monsterViewModel: MonsterViewModel = viewModel()
    val screens = listOf(Screen.Home, Screen.Explore, Screen.Favorites)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        // ini tab bar yang di bawah itu, buat navigasi
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                screens.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // tempat ngatur rute-rute halaman aplikasi
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                MonsterListScreen(navController, monsterViewModel)
            }
            composable(Screen.Explore.route) {
                ExploreScreen(navController, monsterViewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(navController, monsterViewModel)
            }
            // halaman buat munculin detail monster pas diklik
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                val uiState = monsterViewModel.uiState.value
                val exploreMonsters = monsterViewModel.exploreMonsters.value
                val favoriteMonsters = monsterViewModel.favoriteMonsters
                val serpImageUrl = id?.let { monsterViewModel.monsterImages[it] }

                // nyari data monsternya di semua list yang ada biar ga blank
                val monster = if (uiState is MonsterUiState.Success) {
                    uiState.monsters.find { it.id == id }
                } else null ?: exploreMonsters.find { it.id == id }
                ?: favoriteMonsters.find { it.id == id }

                if (monster != null) {
                    DetailMonsterScreen(
                        monster = monster,
                        serpImageUrl = serpImageUrl,
                        navController = navController
                    )
                } else {
                    // kalo belum ketemu munculin muter-muter dulu
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}

// halaman utama yang isinya list monster banyak banget
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonsterListScreen(navController: NavController, viewModel: MonsterViewModel) {
    val uiState by viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Monster Hunter", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.fetchMonsters() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // kolom buat ngetik cari nama monster
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    if (it.length >= 3) viewModel.fetchMonsters(it)
                    else if (it.isEmpty()) viewModel.fetchMonsters()
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cari Nama Monster...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // munculin isi kontennya (loading, error, apa list monster)
            when (uiState) {
                is MonsterUiState.Loading -> LoadingIndicator()
                is MonsterUiState.Error -> ErrorContent((uiState as MonsterUiState.Error).message) { viewModel.fetchMonsters() }
                is MonsterUiState.Success -> {
                    val state = uiState as MonsterUiState.Success
                    MonsterListContent(
                        monsters = state.monsters,
                        viewModel = viewModel,
                        navController = navController,
                        snackbarHostState = snackbarHostState,
                        hasReachedEnd = state.hasReachedEnd,
                        onLoadMore = { viewModel.fetchMonsters(isLoadMore = true) }
                    )
                }
            }
        }
    }
}

// halaman buat cari monster berdasarkan map atau kotanya
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(navController: NavController, viewModel: MonsterViewModel) {
    var locationQuery by remember { mutableStateOf("") }
    val maps by viewModel.exploreMaps
    val monsters by viewModel.exploreMonsters
    val isExploring by viewModel.isExploring
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Explore Map", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            OutlinedTextField(
                value = locationQuery,
                onValueChange = { 
                    locationQuery = it
                    viewModel.searchMaps(it)
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Cari Nama Map/Kota...") },
                leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (isExploring) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                }
            }

            // nampilin daftar monster di map itu atau pilihan map-nya
            if (monsters.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Monster di Lokasi Ini:", style = MaterialTheme.typography.titleSmall)
                    TextButton(onClick = { viewModel.searchMaps(locationQuery) }) {
                        Text("Kembali ke Daftar Map")
                    }
                }
                Box(modifier = Modifier.weight(1f)) {
                    MonsterListContent(
                        monsters = monsters,
                        viewModel = viewModel,
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
            } else if (maps.isNotEmpty()) {
                Text("Pilih Map/Lokasi Spesifik:", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall)
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(maps) { map ->
                        ListItem(
                            headlineContent = { Text(map.name) },
                            modifier = Modifier.clickable { viewModel.fetchMonstersByMap(map.id) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, null) }
                        )
                        Divider()
                    }
                }
            } else if (!isExploring) {
                EmptyState("Ketik nama map untuk mencari lokasi monster.")
            }
        }
    }
}

// halaman buat ngeliat monster-monster yang udah kita kasih love/favorit
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController, viewModel: MonsterViewModel) {
    val favorites = viewModel.favoriteMonsters
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Monster Favorit", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        if (favorites.isEmpty()) {
            EmptyState("Belum ada monster favorit.")
        } else {
            Box(modifier = Modifier.padding(innerPadding)) {
                MonsterListContent(favorites, viewModel, navController, snackbarHostState)
            }
        }
    }
}

// komponen buat nampilin list monster pake scroll (lazy column)
@Composable
fun MonsterListContent(
    monsters: List<MonsterApiModel>,
    viewModel: MonsterViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    hasReachedEnd: Boolean = true,
    onLoadMore: () -> Unit = {}
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(monsters) { monster ->
            val serpImageUrl = viewModel.monsterImages[monster.id]
            LaunchedEffect(monster.id) {
                viewModel.fetchImageForMonster(monster.id, monster.name)
            }
            MonsterCard(
                monster = monster,
                serpImageUrl = serpImageUrl,
                isFavorite = viewModel.isFavorite(monster.id),
                onFavoriteClick = { viewModel.toggleFavorite(monster) },
                navController = navController,
                snackbarHostState = snackbarHostState
            )
        }

        // ini paginasi, kalo udah mentok bawah tarik data lagi biar ga berat
        if (!hasReachedEnd) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                LaunchedEffect(Unit) {
                    onLoadMore()
                }
            }
        }
    }
}

// desain kartu monster satu biji
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonsterCard(
    monster: MonsterApiModel,
    serpImageUrl: String?,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = { navController.navigate("detail/${monster.id}") }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // nampilin gambar monsternya
            AsyncImage(
                model = serpImageUrl ?: monster.imageUrl,
                contentDescription = monster.name,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit,
                error = painterResource(id = android.R.drawable.ic_menu_report_image)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(monster.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("📍 ${monster.mapName ?: "???"}", style = MaterialTheme.typography.bodySmall)
                Text("Lv. ${monster.level ?: "?"}", color = MaterialTheme.colorScheme.primary)
            }
            // tombol love/favorit
            IconButton(onClick = {
                onFavoriteClick()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(if (!isFavorite) "Ditambah ke favorit" else "Dihapus dari favorit")
                }
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

// buletan loading pas nunggu data
@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

// tampilan kalo ga ada data sama sekali
@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary)
    }
}

// tampilan kalo internet mati atau error
@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(message, color = MaterialTheme.colorScheme.error, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}
