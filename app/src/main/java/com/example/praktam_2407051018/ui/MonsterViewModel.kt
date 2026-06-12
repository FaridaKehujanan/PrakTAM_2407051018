package com.example.praktam_2407051018.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2407051018.data.model.MapApiModel
import com.example.praktam_2407051018.data.model.MonsterApiModel
import com.example.praktam_2407051018.data.repository.MonsterRepository
import kotlinx.coroutines.launch

// bungkus status UI (lagi loading, sukses, apa error)
sealed class MonsterUiState {
    object Loading : MonsterUiState()
    data class Success(val monsters: List<MonsterApiModel>, val hasReachedEnd: Boolean = false) : MonsterUiState()
    data class Error(val message: String) : MonsterUiState()
}

class MonsterViewModel(private val repository: MonsterRepository = MonsterRepository()) : ViewModel() {

    // nyimpen status UI sekarang
    private val _uiState = mutableStateOf<MonsterUiState>(MonsterUiState.Loading)
    val uiState: State<MonsterUiState> = _uiState

    // wadah buat nyimpen gambar monster biar ga download ulang-ulang
    private val _monsterImages = mutableStateMapOf<String, String?>()
    val monsterImages: Map<String, String?> = _monsterImages

    // list monster yang disukai user
    private val _favoriteMonsters = mutableStateListOf<MonsterApiModel>()
    val favoriteMonsters: List<MonsterApiModel> = _favoriteMonsters

    // buat urusan cari-cari map di menu explore
    private val _exploreMaps = mutableStateOf<List<MapApiModel>>(emptyList())
    val exploreMaps: State<List<MapApiModel>> = _exploreMaps

    // monster yang ketemu pas nyari lewat map
    private val _exploreMonsters = mutableStateOf<List<MonsterApiModel>>(emptyList())
    val exploreMonsters: State<List<MonsterApiModel>> = _exploreMonsters

    // status lagi nyari map apa ngga
    private val _isExploring = mutableStateOf(false)
    val isExploring: State<Boolean> = _isExploring

    // buat ngatur paginasi (biar scrollnya nyambung terus)
    private var currentOffset = 0
    private val limit = 20
    private var currentQuery: String? = null

    init {
        // pas baru buka langsung tarik data
        fetchMonsters()
    }

    // fungsi utama buat ambil data monster, bisa buat search juga
    fun fetchMonsters(name: String? = null, isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            _uiState.value = MonsterUiState.Loading
            currentOffset = 0
            currentQuery = name
        }

        viewModelScope.launch {
            try {
                val newMonsters = repository.getMonsters(name = currentQuery, offset = currentOffset, limit = limit)
                
                val currentMonsters = if (isLoadMore && _uiState.value is MonsterUiState.Success) {
                    (_uiState.value as MonsterUiState.Success).monsters
                } else {
                    emptyList()
                }

                // gabungin data lama sama yang baru dapet (paginasi)
                val updatedList = currentMonsters + newMonsters
                currentOffset += newMonsters.size
                
                _uiState.value = MonsterUiState.Success(
                    monsters = updatedList,
                    hasReachedEnd = newMonsters.size < limit
                )
            } catch (e: Exception) {
                if (!isLoadMore) {
                    _uiState.value = MonsterUiState.Error("Gagal memuat data: ${e.localizedMessage ?: "Cek koneksi internet"}")
                }
            }
        }
    }

    // nyari nama tempat/map pas diketik user
    fun searchMaps(query: String) {
        if (query.isBlank()) {
            _exploreMaps.value = emptyList()
            _exploreMonsters.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isExploring.value = true
            _exploreMonsters.value = emptyList()
            _exploreMaps.value = repository.searchMaps(query)
            _isExploring.value = false
        }
    }

    // tarik semua monster yang nongkrong di map itu
    fun fetchMonstersByMap(mapId: String) {
        viewModelScope.launch {
            _isExploring.value = true
            _exploreMonsters.value = repository.getMonstersByMap(mapId)
            _isExploring.value = false
        }
    }

    // download gambar monster dari google biar keren
    fun fetchImageForMonster(monsterId: String, monsterName: String) {
        if (_monsterImages.containsKey(monsterId)) return
        viewModelScope.launch {
            val imageUrl = repository.getMonsterImage(monsterName)
            _monsterImages[monsterId] = imageUrl
        }
    }

    // buat nambahin atau hapus monster dari list favorit
    fun toggleFavorite(monster: MonsterApiModel) {
        if (_favoriteMonsters.any { it.id == monster.id }) {
            _favoriteMonsters.removeAll { it.id == monster.id }
        } else {
            _favoriteMonsters.add(monster)
        }
    }

    // ngecek monster ini udah masuk favorit apa belum
    fun isFavorite(monsterId: String): Boolean {
        return _favoriteMonsters.any { it.id == monsterId }
    }
}
