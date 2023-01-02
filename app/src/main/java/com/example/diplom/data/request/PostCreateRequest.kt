package com.example.diplom.data.request

import com.example.diplom.data.model.AttachmentData
import com.example.diplom.data.model.CoordsData
import com.google.gson.annotations.SerializedName

data class PostCreateRequest(
    @SerializedName("attachment")
    val attachment: AttachmentData? = null,
    @SerializedName("content")
    val content: String,
    @SerializedName("coords")
    val coords: CoordsData? = null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("link")
    val link: String? = null,
    @SerializedName("mentionIds")
    val mentionIds: List<Int>? = null
)