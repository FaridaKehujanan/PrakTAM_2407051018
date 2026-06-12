package com.example.praktam_2407051018.data.repository

import com.example.praktam_2407051018.data.api.MonsterApiService
import com.example.praktam_2407051018.data.api.SerpApiService
import com.example.praktam_2407051018.data.model.MapApiModel
import com.example.praktam_2407051018.data.model.MonsterApiModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MonsterRepository {
    // konek ke api monster toram
    private val monsterApiService: MonsterApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://coryn.club/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MonsterApiService::class.java)
    }

    // konek ke api buat nyari gambar di google
    private val serpApiService: SerpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://serpapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SerpApiService::class.java)
    }

    // naruh api key serpapi di sini biar jalan
    private val SERP_API_KEY = "enter your api here"

    // ambil list monster dari internet, ada limit sama offset buat scroll-scroll
    suspend fun getMonsters(name: String? = null, offset: Int = 0, limit: Int = 20): List<MonsterApiModel> {
        return try {
            val response = monsterApiService.getMonsters(name = name, offset = offset, limit = limit)
            if (response.success) response.data else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // buat nyari nama map/tempat monster tinggal
    suspend fun searchMaps(query: String): List<MapApiModel> {
        return try {
            val response = monsterApiService.getMaps(query)
            if (response.success) response.data else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ambil monster apa aja yang ada di dalem satu map tertentu
    suspend fun getMonstersByMap(mapId: String): List<MonsterApiModel> {
        return try {
            val response = monsterApiService.getMonsters(mapId = mapId, limit = 100)
            if (response.success) response.data else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // nyari foto monster lewat google images biar tampilannya cakep
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
