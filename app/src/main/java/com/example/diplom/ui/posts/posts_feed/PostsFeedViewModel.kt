package com.example.diplom.ui.posts.posts_feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.PostRepository
import com.example.diplom.domain.entity.Attachment
import com.example.diplom.domain.entity.Post
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsFeedViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _postsData: MutableLiveData<List<Post>> = MutableLiveData()
    val postsData: LiveData<List<Post>>
        get() = _postsData

    private val _playingMediaPost: MutableLiveData<Post?> = MutableLiveData()
    val playingMediaPost: LiveData<Post?>
        get() = _playingMediaPost

    val navigateToAddPost = SingleLiveEvent<Unit>()

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(true)
    }

    fun getPosts() {
        viewModelScope.launch {
            postRepository.getPosts()
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        result.data?.let { _postsData.postValue(it) }
                    } else {
                        Log.e("getPosts", result.error?.statusMessage.orEmpty())
                    }
                }

        }
    }

    fun onLikeClicked(post: Post) {
        viewModelScope.launch {
            if (post.likedByMe) {
                postRepository.dislikePost(post.id.toString())
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updatePost(it) }
                        } else {
                            Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            } else {
                postRepository.likePost(post.id.toString())
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updatePost(it) }
                        } else {
                            Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            }
        }
    }

    fun onRemoveClicked(post: Post) {
        viewModelScope.launch {
            postRepository.removePost(post.id.toString())
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        getPosts()
                    } else {
                        Log.e("onDeleteListener", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }

    fun onMediaClicked(post: Post) {
        val state = when {
            (post.id == playingMediaPost.value?.id) && (post.attachment?.state == STATE.PLAY) -> STATE.PAUSE
            (playingMediaPost.value == null) || (post.attachment?.state == STATE.PAUSE) || (post.id != playingMediaPost.value?.id) -> STATE.PLAY
            else -> STATE.NOT_PLAY
        }
        val attachment = post.attachment?.let { Attachment(it.type, post.attachment.url, state) }
        val newPost = post.copy(attachment = attachment)
        if (attachment?.state != STATE.NOT_PLAY) _playingMediaPost.postValue(newPost)
        else _playingMediaPost.postValue(null)
        updatePost(newPost)
    }


    fun onFinishMedia() {
        val attachment = _playingMediaPost.value?.attachment?.let { Attachment(it.type, it.url, STATE.NOT_PLAY) }
        val newPost = _playingMediaPost.value?.copy(attachment = attachment)
        if (newPost != null) {
            updatePost(newPost)
        }
        _playingMediaPost.postValue(null)
    }

    private fun updatePost(post: Post) {
        val items: MutableList<Post> = postsData.value
            ?.map { updatedPost ->
                if (updatedPost.id == post.id) {
                    post
                } else {
                    val attachment = updatedPost.attachment?.let { Attachment(it.type, it.url, STATE.NOT_PLAY) }
                    updatedPost.copy(attachment = attachment)
                }
            }
            ?.toMutableList() ?: mutableListOf()
        _postsData.postValue(items)
    }
}