package com.example.diplom.domain.entity

import java.io.Serializable

data class Post(
    val attachment: Attachment?,
    val author: String,
    val authorAvatar: String?,
    val authorId: Int,
    val authorJob: String?,
    val content: String,
    val coords: Coords?,
    val id: Int,
    val likeOwnerIds: List<Int>?,
    val likedByMe: Boolean,
    val link: String?,
    val mentionIds: List<Int>?,
    val mentionedMe: Boolean,
    val ownedByMe: Boolean,
    val published: String,
) : Serializable