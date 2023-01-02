package com.example.diplom.data.mapper

import com.example.diplom.domain.entity.AttachmentType

object AttachmentTypeMapper {
    fun mapDataToDomain(data : String): AttachmentType {
        return when(data) {
            "IMAGE" -> AttachmentType.IMAGE
            "AUDIO" -> AttachmentType.AUDIO
            "VIDEO" -> AttachmentType.VIDEO
            else -> AttachmentType.UNKNOWN
        }
    }
}