package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class TokenData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("token")
    val token: String,
)