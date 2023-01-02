package com.example.diplom.domain.entity

import java.io.Serializable

data class Event(
    val attachment: Attachment?,
    val author: String,
    val authorAvatar: String?,
    val authorId: Int,
    val authorJob: String?,
    val content: String,
    val coords: Coords?,
    val datetime: String,
    val id: Int,
    val likeOwnerIds: List<Int>?,
    val likedByMe: Boolean,
    val link: String?,
    val ownedByMe: Boolean,
    val participantsIds: List<Int>?,
    val participatedByMe: Boolean,
    val published: String,
    val speakerIds: List<Int>?,
    val type: String,
) : Serializable