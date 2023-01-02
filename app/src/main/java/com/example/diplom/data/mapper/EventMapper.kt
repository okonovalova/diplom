package com.example.diplom.data.mapper

import com.example.diplom.data.model.EventData
import com.example.diplom.domain.entity.Attachment
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Event

object EventMapper {
    fun mapListDataToDomain(data: List<EventData>?): List<Event> {
        return data?.map { mapDataToDomain(it) } ?: emptyList()
    }

    fun mapDataToDomain(data: EventData?): Event {
        if (data == null) throw IllegalArgumentException("EventData must be not null")
        return Event(
            attachment = data.attachment?.let {
                Attachment(AttachmentTypeMapper.mapDataToDomain(it.type), it.url)
            },
            author = data.author,
            authorAvatar = data.authorAvatar,
            authorId = data.authorId,
            authorJob = data.authorJob,
            content = data.content,
            coords = data.coords?.let {
                Coords(it.lat, it.long)
            },
            id = data.id,
            likeOwnerIds = data.likeOwnerIds,
            likedByMe = data.likedByMe,
            link = data.link,
            ownedByMe = data.ownedByMe,
            published = data.published,
            datetime = data.datetime,
            participantsIds = data.participantsIds,
            participatedByMe = data.participatedByMe,
            type = data.type,
            speakerIds = data.speakerIds
        )
    }
}