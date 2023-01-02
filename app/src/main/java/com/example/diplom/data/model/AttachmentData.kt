package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class AttachmentData(
    @SerializedName("type")
    val type: String,
    @SerializedName("url")
    val url: String
)