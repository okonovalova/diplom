package com.example.diplom.data.request

import com.google.gson.annotations.SerializedName

data class AuthentificationRequest(
    @SerializedName("login")
    val login: String,
    @SerializedName("password")
    val password: String,
)