package com.example.diplom.data.api

import com.example.diplom.data.request.AuthentificationRequest
import com.example.diplom.data.model.TokenData
import com.example.diplom.data.model.UserData
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserService {
    @POST("api/users/authentication")
    suspend fun authenticate(@Body authenticationRequest: AuthentificationRequest): Response<TokenData>

    @Multipart
    @POST("api/users/registration")
    suspend fun registrate(
        @Part("login") login: RequestBody,
        @Part("name") name: RequestBody,
        @Part("password") password: RequestBody,
        @Part("file\"; filename=\"user.png\" ") file: RequestBody?
    ): Response<TokenData>

    @GET("api/users/{user_id}")
    suspend fun getUserInfo(@Path("user_id") userId: String): Response<UserData>
}