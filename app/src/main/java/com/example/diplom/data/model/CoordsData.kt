package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class CoordsData(
    @SerializedName("lat")
    val lat: String,
    @SerializedName("long")
    val long: String
)