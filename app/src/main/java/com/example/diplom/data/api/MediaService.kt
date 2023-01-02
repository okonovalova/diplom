package com.example.diplom.data.api

import com.example.diplom.data.model.MediaData
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaService {
    @Multipart
    @POST("api/media")
    suspend fun downloadFile(
        @Part("file\"; filename=\"user.png\" ") file: RequestBody
    ): Response<MediaData>
}