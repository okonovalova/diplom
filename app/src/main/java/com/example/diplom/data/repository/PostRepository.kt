package com.example.diplom.data.repository

import com.example.diplom.data.api.PostService
import com.example.diplom.data.mapper.PostMapper
import com.example.diplom.data.model.AttachmentData
import com.example.diplom.data.model.CoordsData
import com.example.diplom.data.network.BaseRemoteRepository
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.prefs.PreferenceService
import com.example.diplom.data.request.PostCreateRequest
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postService: PostService,
    private val preferenceService: PreferenceService,
) : BaseRemoteRepository() {

    private val cache : MutableList<Post> = mutableListOf()

    suspend fun getPosts(): Flow<DataResult<List<Post>>> {
        return flow {
            val result = getResult(
                request = { postService.getPosts() },
                mapTo = PostMapper::mapListDataToDomain
            )
            result.data?.let {
                cache.clear()
                cache.addAll(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun createPost(content: String, link: String?, imageUrl: String?, coords: Coords?): Flow<DataResult<Post>> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val body = PostCreateRequest(
            id = 0,
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData
        )
        return flow {
            val result = getResult(
                request = { postService.createPost(body) },
                mapTo = PostMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun likePost(id: String): Flow<DataResult<Post>> {
        return flow {
            val result = getResult(
                request = { postService.likePost(id) },
                mapTo = PostMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun  editPost(id: String, content: String, link: String?, imageUrl: String?, coords: Coords?): Flow<DataResult<Post>> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val body = PostCreateRequest(
            id = id.toInt(),
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData
        )
        return flow {
            val result = getResult(
                request = { postService.createPost(body) },
                mapTo = PostMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun dislikePost(id: String): Flow<DataResult<Post>> {
        return flow {
            val result = getResult(
                request = { postService.dislikePost(id) },
                mapTo = PostMapper::mapDataToDomain
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun removePost(id: String): Flow<DataResult<Unit>> {
        return flow {
            val result = getResult(
                request = { postService.removePost(id) },
                mapTo = {}
            )
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getMyPosts (): Flow<DataResult<List<Post>>> {
        return flow {
            val result = getResult(
                request = { postService.getMyPosts(preferenceService.userId.toString()) },
                mapTo = PostMapper::mapListDataToDomain
            )
            result.data?.let {
                cache.clear()
                cache.addAll(it)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }
}