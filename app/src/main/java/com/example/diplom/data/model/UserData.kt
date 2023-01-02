package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("login")
    val login: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("avatar")
    val avatar: String?,
)