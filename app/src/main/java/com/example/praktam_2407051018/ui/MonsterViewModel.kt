package com.example.praktam_2407051018.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.praktam_2407051018.data.model.MonsterApiModel
import com.example.praktam_2407051018.data.repository.MonsterRepository
import kotlinx.coroutines.launch

sealed class MonsterUiState {
    object Loading : MonsterUiState()
    data class Success(val monsters: List<MonsterApiModel>) : MonsterUiState()
    data class Error(val message: String) : MonsterUiState()
}

class MonsterViewModel(private val repository: MonsterRepository = MonsterRepository()) : ViewModel() {

    private val _uiState = mutableStateOf<MonsterUiState>(MonsterUiState.Loading)
    val uiState: State<MonsterUiState> = _uiState

    // Map untuk menyimpan URL gambar dari SerpApi (Monster ID -> Image URL)
    private val _monsterImages = mutableStateMapOf<String, String?>()
    val monsterImages: Map<String, String?> = _monsterImages

    init {
        fetchMonsters()
    }

    fun fetchMonsters(name: String? = null) {
        viewModelScope.launch {
            _uiState.value = MonsterUiState.Loading
            try {
                val monsters = repository.getMonsters(name = name)
                if (monsters.isEmpty()) {
                    _uiState.value = MonsterUiState.Error("Tidak ada monster ditemukan")
                } else {
                    _uiState.value = MonsterUiState.Success(monsters)
                    // (Opsional) Bisa mulai fetch gambar secara background di sini
                    // Namun untuk hemat kuota API, kita fetch saat dibutuhkan saja
                }
            } catch (e: Exception) {
                _uiState.value = MonsterUiState.Error("Gagal memuat data: ${e.localizedMessage ?: "Cek koneksi internet"}")
            }
        }
    }

    fun fetchImageForMonster(monsterId: String, monsterName: String) {
        if (_monsterImages.containsKey(monsterId)) return // Sudah pernah fetch

        viewModelScope.launch {
            val imageUrl = repository.getMonsterImage(monsterName)
            _monsterImages[monsterId] = imageUrl
        }
    }
}
