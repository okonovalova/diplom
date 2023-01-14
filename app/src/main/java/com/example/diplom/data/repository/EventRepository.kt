package com.example.diplom.data.repository

import com.example.diplom.data.api.EventService
import com.example.diplom.data.mapper.EventMapper
import com.example.diplom.data.model.AttachmentData
import com.example.diplom.data.model.CoordsData
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.request.EventCreateRequest
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventService: EventService
) : BaseRemoteRepository {

    suspend fun getEvents(): DataResult<List<Event>> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.getEvents() },
                mapTo = EventMapper::mapListDataToDomain
            )
        }
    }

    suspend fun createEvent(
        content: String,
        link: String?,
        imageUrl: String?,
        coords: Coords?,
        type: String,
        eventDateTime: String
    ): DataResult<Event> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val eventType = type
        val body = EventCreateRequest(
            id = 0,
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData,
            type = eventType,
            datetime = eventDateTime,

            )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.createEvent(body) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun editEvent(
        id: String,
        content: String,
        link: String?,
        imageUrl: String?,
        coords: Coords?,
        type: String,
        eventDateTime: String
    ): DataResult<Event> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val eventType = type
        val body = EventCreateRequest(
            id = id.toInt(),
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData,
            type = eventType,
            datetime = eventDateTime,

            )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.createEvent(body) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun likeEvent(id: Int): DataResult<Event> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.likeEvent(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun dislikeEvent(id: Int): DataResult<Event> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.dislikeEvent(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun addParticipation(id: Int): DataResult<Event> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.addParticipation(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun deleteParticipation(id: Int): DataResult<Event> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.deleteParticipation(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
        }
    }

    suspend fun removeEvent(id: String): DataResult<Unit> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { eventService.removeEvent(id) },
                mapTo = {}
            )
        }
    }
}