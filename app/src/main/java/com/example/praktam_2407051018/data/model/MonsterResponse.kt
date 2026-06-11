package com.example.praktam_2407051018.data.model

import com.google.gson.annotations.SerializedName

data class MonsterResponse(
    val success: Boolean,
    val data: List<MonsterApiModel>,
    val meta: MetaData
)

data class MonsterApiModel(
    val id: String,
    val name: String,
    val level: String?,
    val type: String?,
    val mode: String?,
    @SerializedName("map_name")
    val mapName: String?
) {
    // Helper to get image URL if it follows the pattern
    val imageUrl: String
        get() = "https://coryn.club/images/monster/$id.png"
}

data class MetaData(
    val total: Int,
    val limit: Int,
    val offset: Int
)
