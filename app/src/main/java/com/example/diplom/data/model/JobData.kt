package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class JobData (
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("position")
    val position: String,
    @SerializedName("start")
    val start: String,
    @SerializedName("finish")
    val finish:	String?,
    @SerializedName("link")
    val link: String?,
)