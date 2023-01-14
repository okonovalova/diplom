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
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRepository @Inject constructor(
    private val postService: PostService,
    private val preferenceService: PreferenceService,
) : BaseRemoteRepository {

    private val cache: MutableList<Post> = mutableListOf()

    suspend fun getPosts(): DataResult<List<Post>> {
        return withContext(Dispatchers.Default) {
            val result = getResult(
                request = { postService.getPosts() },
                mapTo = PostMapper::mapListDataToDomain
            )
            result.data?.let {
                cache.clear()
                cache.addAll(it)
            }
            result
        }
    }

    suspend fun createPost(content: String, link: String?, imageUrl: String?, coords: Coords?): DataResult<Post> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val body = PostCreateRequest(
            id = 0,
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData
        )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { postService.createPost(body) },
                mapTo = PostMapper::mapDataToDomain
            )
        }
    }

    suspend fun likePost(id: String): DataResult<Post> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { postService.likePost(id) },
                mapTo = PostMapper::mapDataToDomain
            )
        }
    }

    suspend fun editPost(id: String, content: String, link: String?, imageUrl: String?, coords: Coords?): DataResult<Post> {
        val attachment = if (imageUrl == null) null else AttachmentData("IMAGE", imageUrl)
        val coordsData = if (coords == null) null else CoordsData(coords.lat, coords.long)
        val body = PostCreateRequest(
            id = id.toInt(),
            content = content,
            link = link,
            attachment = attachment,
            coords = coordsData
        )
        return withContext(Dispatchers.Default) {
            getResult(
                request = { postService.createPost(body) },
                mapTo = PostMapper::mapDataToDomain
            )
        }
    }

    suspend fun dislikePost(id: String): DataResult<Post> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { postService.dislikePost(id) },
                mapTo = PostMapper::mapDataToDomain
            )
        }
    }

    suspend fun removePost(id: String): DataResult<Unit> {
        return withContext(Dispatchers.Default) {
            getResult(
                request = { postService.removePost(id) },
                mapTo = {}
            )
        }
    }

    suspend fun getMyPosts(): DataResult<List<Post>> {
        return withContext(Dispatchers.Default) {
            val result = getResult(
                request = { postService.getMyPosts(preferenceService.userId.toString()) },
                mapTo = PostMapper::mapListDataToDomain
            )
            result.data?.let {
                cache.clear()
                cache.addAll(it)
            }
            result
        }
    }
}