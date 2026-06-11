package com.example.praktam_2407051018.data.api

import com.example.praktam_2407051018.data.model.SerpApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SerpApiService {
    @GET("search")
    suspend fun searchImages(
        @Query("q") query: String,
        @Query("engine") engine: String = "google_images",
        @Query("api_key") apiKey: String
    ): SerpApiResponse
}
