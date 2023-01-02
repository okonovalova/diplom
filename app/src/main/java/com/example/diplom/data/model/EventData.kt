package com.example.diplom.data.model

import com.google.gson.annotations.SerializedName

data class EventData(
    @SerializedName("attachment")
    val attachment: AttachmentData?,
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
    val coords: CoordsData?,
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("likeOwnerIds")
    val likeOwnerIds: List<Int>?,
    @SerializedName("likedByMe")
    val likedByMe: Boolean,
    @SerializedName("link")
    val link: String?,
    @SerializedName("ownedByMe")
    val ownedByMe: Boolean,
    @SerializedName("participantsIds")
    val participantsIds: List<Int>?,
    @SerializedName("participatedByMe")
    val participatedByMe: Boolean,
    @SerializedName("published")
    val published: String,
    @SerializedName("speakerIds")
    val speakerIds: List<Int>?,
    @SerializedName("type")
    val type: String,
)