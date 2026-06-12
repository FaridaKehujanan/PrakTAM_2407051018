package com.example.praktam_2407051018.data.model

data class MapResponse(
    val success: Boolean,
    val data: List<MapApiModel>
)

data class MapApiModel(
    val id: String,
    val name: String
)
