package com.example.diplom.data.mapper

import com.example.diplom.data.model.PostData
import com.example.diplom.domain.entity.Attachment
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Post

object PostMapper {
    fun mapListDataToDomain(data: List<PostData>?): List<Post> {
        return data?.map { mapDataToDomain(it) } ?: emptyList()
    }

    fun mapDataToDomain(data: PostData?): Post {
        if (data == null) throw IllegalArgumentException("PostData must be not null")
        return Post(
            attachment = data.attachmentData?.let {
                Attachment(AttachmentTypeMapper.mapDataToDomain(it.type), it.url)
            },
            author = data.author,
            authorAvatar = data.authorAvatar,
            authorId = data.authorId,
            authorJob = data.authorJob,
            content = data.content,
            coords = data.coordsData?.let {
                Coords(it.lat, it.long)
            },
            id = data.id,
            likeOwnerIds = data.likeOwnerIds,
            likedByMe = data.likedByMe,
            link = data.link,
            mentionIds = data.mentionIds,
            mentionedMe = data.mentionedMe,
            ownedByMe = data.ownedByMe,
            published = data.published
        )
    }
}
