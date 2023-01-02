package com.example.diplom.data.repository

import com.example.diplom.data.api.EventService
import com.example.diplom.data.mapper.EventMapper
import com.example.diplom.data.model.AttachmentData
import com.example.diplom.data.model.CoordsData
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.network.ResultError
import com.example.diplom.data.request.EventCreateRequest
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventService: EventService
) : BaseRemoteRepository() {

    suspend fun getEvents(): Flow<DataResult<List<Event>>> {
        return flow {
            val result = getResult(
                request = { eventService.getEvents() },
                mapTo = EventMapper::mapListDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createEvent(
        content: String,
        link: String?,
        imageUrl: String?,
        coords: Coords?,
        type: String,
        eventDateTime: String
    ): Flow<DataResult<Event>> {
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
        return flow {
            val result = getResult(
                request = { eventService.createEvent(body) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun editEvent(
        id: String,
        content: String,
        link: String?,
        imageUrl: String?,
        coords: Coords?,
        type: String,
        eventDateTime: String
    ): Flow<DataResult<Event>> {
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
        return flow {
            val result = getResult(
                request = { eventService.createEvent(body) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun likeEvent(id: Int): Flow<DataResult<Event>> {
        return flow {
            val result = getResult(
                request = { eventService.likeEvent(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }
            .flowOn(Dispatchers.IO)
    }

    suspend fun dislikeEvent(id: Int): Flow<DataResult<Event>> {
        return flow {
            val result = getResult(
                request = { eventService.dislikeEvent(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }
            .flowOn(Dispatchers.IO)
            .catch {
                emit(DataResult.error(ResultError(0, it.toString())))
            }
    }

    suspend fun addParticipation(id: Int): Flow<DataResult<Event>> {
        return flow {
            val result = getResult(
                request = { eventService.addParticipation(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun deleteParticipation(id: Int): Flow<DataResult<Event>> {
        return flow {
            val result = getResult(
                request = { eventService.deleteParticipation(id.toString()) },
                mapTo = EventMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun removeEvent(id: String): Flow<DataResult<Unit>> {
        return flow {
            val result = getResult(
                request = { eventService.removeEvent(id) },
                mapTo = {}
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}