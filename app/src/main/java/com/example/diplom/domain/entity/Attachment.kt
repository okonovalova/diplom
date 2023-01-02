package com.example.diplom.domain.entity

import java.io.Serializable

data class Attachment(
    val type: AttachmentType,
    val url: String,
    val state: STATE = STATE.NOT_PLAY
) : Serializable
enum class STATE {
    PLAY, PAUSE, NOT_PLAY
}