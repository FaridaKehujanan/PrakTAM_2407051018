package com.example.praktam_2407051018.data.model

import com.google.gson.annotations.SerializedName

data class SerpApiResponse(
    @SerializedName("images_results")
    val imagesResults: List<ImageResult>?
)

data class ImageResult(
    val original: String?,
    val thumbnail: String?
)
