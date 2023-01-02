package com.example.diplom.data.api

import com.example.diplom.data.model.PostData
import com.example.diplom.data.request.PostCreateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostService {

    @GET("api/posts")
    suspend fun getPosts(): Response<List<PostData>>

    @GET("/api/{author_id}/wall")
    suspend fun getMyPosts(@Path("author_id") authorId: String): Response<List<PostData>>

    @POST("api/posts")
    suspend fun createPost(@Body postCreateRequest: PostCreateRequest): Response<PostData>

    @POST("api/posts/{post_id}/likes")
    suspend fun likePost(@Path("post_id") postId: String): Response<PostData>

    @DELETE("api/posts/{post_id}/likes")
    suspend fun dislikePost(@Path("post_id") postId: String): Response<PostData>

    @DELETE("api/posts/{post_id}")
    suspend fun removePost(@Path("post_id") postId: String): Response<Unit>
}