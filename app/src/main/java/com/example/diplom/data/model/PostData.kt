package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class PostData(
    @SerializedName("attachment")
    val attachmentData: AttachmentData?,
    @SerializedName("author")
    val author: String,
    @SerializedName("authorAvatar")
    val authorAvatar: String?,
    @SerializedName("authorId")
    val authorId: Int,
    @SerializedName("authorJob")
    val authorJob: String?,
    @SerializedName("content")
    val content: String,
    @SerializedName("coords")
    val coordsData: CoordsData?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Int>?,
    @SerializedName("likedByMe")
    val likedByMe: Boolean,
    @SerializedName("link")
    val link: String?,
    @SerializedName("mentionIds")
    val mentionIds: List<Int>?,
    @SerializedName("mentionedMe")
    val mentionedMe: Boolean,
    @SerializedName("ownedByMe")
    val ownedByMe: Boolean,
    @SerializedName("published")
    val published: String,
)