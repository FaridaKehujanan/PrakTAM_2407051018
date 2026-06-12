package com.example.praktam_2407051018.data.api

import com.example.praktam_2407051018.data.model.MapResponse
import com.example.praktam_2407051018.data.model.MonsterResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MonsterApiService {
    @GET("api/v1/monsters.php")
    suspend fun getMonsters(
        @Query("name") name: String? = null,
        @Query("map_id") mapId: String? = null,
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): MonsterResponse

    @GET("api/v1/maps.php")
    suspend fun getMaps(
        @Query("name") name: String
    ): MapResponse
}
