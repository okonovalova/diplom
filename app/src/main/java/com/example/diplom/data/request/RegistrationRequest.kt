package com.example.diplom.data.request

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("login")
    val login: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("name")
    val name: String,
)