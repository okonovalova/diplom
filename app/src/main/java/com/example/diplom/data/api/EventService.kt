package com.example.diplom.data.api

import com.example.diplom.data.model.EventData
import com.example.diplom.data.request.EventCreateRequest
import retrofit2.Response
import retrofit2.http.*

interface EventService {

    @GET("api/events")
    suspend fun getEvents(): Response<List<EventData>>

    @POST("api/events")
    suspend fun createEvent(@Body eventCreateRequest: EventCreateRequest): Response<EventData>

    @POST("api/events/{event_id}/likes")
    suspend fun likeEvent(@Path("event_id") eventId: String): Response<EventData>

    @DELETE("api/events/{event_id}/likes")
    suspend fun dislikeEvent(@Path("event_id") eventId: String): Response<EventData>

    @POST("api/events/{event_id}/participants")
    suspend fun addParticipation(@Path("event_id") eventId: String): Response<EventData>

    @DELETE("api/events/{event_id}/participants")
    suspend fun deleteParticipation(@Path("event_id") eventId: String): Response<EventData>

    @DELETE("api/events/{event_id}")
    suspend fun removeEvent(@Path("event_id") eventId: String): Response<Unit>
}