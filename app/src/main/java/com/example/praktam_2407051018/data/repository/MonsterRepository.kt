package com.example.praktam_2407051018.data.repository

import com.example.praktam_2407051018.data.api.MonsterApiService
import com.example.praktam_2407051018.data.api.SerpApiService
import com.example.praktam_2407051018.data.model.MonsterApiModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MonsterRepository {
    private val monsterApiService: MonsterApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://coryn.club/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MonsterApiService::class.java)
    }

    private val serpApiService: SerpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerpApiService::class.java)
    }

    // MASUKKAN API KEY ANDA DI SINI
    private val SERP_API_KEY = "53ddfb4c9a85c9c9230216fc8da6158f73a963b9a513efe582df59083dda9c8c"

    suspend fun getMonsters(name: String? = null, limit: Int = 20): List<MonsterApiModel> {
        val response = monsterApiService.getMonsters(name = name, limit = limit)
        return if (response.success) {
            response.data
        } else {
            emptyList()
        }
    }

    suspend fun getMonsterImage(monsterName: String): String? {
        return try {
            val response = serpApiService.searchImages(
                query = "Toram Online $monsterName",
                apiKey = SERP_API_KEY
            )
            response.imagesResults?.firstOrNull()?.original
        } catch (e: Exception) {
            null
        }
    }
}
