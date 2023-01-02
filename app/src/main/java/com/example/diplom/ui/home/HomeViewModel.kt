package com.example.diplom.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.JobRepository
import com.example.diplom.data.repository.PostRepository
import com.example.diplom.data.repository.UserRepository
import com.example.diplom.domain.entity.*
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val userRepository: UserRepository,
    private val jobRepository: JobRepository,
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _userData: MutableLiveData<User> = MutableLiveData()
    val userData: LiveData<User>
        get() = _userData

    private val _jobsData: MutableLiveData<List<Job>> = MutableLiveData()
    val jobsData: LiveData<List<Job>>
        get() = _jobsData
    val navigateToAddJob = SingleLiveEvent<Unit>()

    private val _postsData: MutableLiveData<List<Post>> = MutableLiveData()
    val postsData: LiveData<List<Post>>
        get() = _postsData
    val navigateToAddPost = SingleLiveEvent<Unit>()

    private val _playingMediaPost: MutableLiveData<Post?> = MutableLiveData()
    val playingMediaPost: LiveData<Post?>
        get() = _playingMediaPost

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(true)
    }

    fun getUserInfo() {
        viewModelScope.launch {
            userRepository.getUserInfo()
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        result.data?.let { _userData.postValue(it) }
                    } else {
                        Log.e("getUserInfo", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }

    fun getJobs() {
        viewModelScope.launch {
            jobRepository.getJobs()
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        result.data?.let { _jobsData.postValue(it) }
                    } else {
                        Log.e("getJobs", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }

    fun onRemoveJobClicked(job: Job) {
        viewModelScope.launch {
            jobRepository.removeJob(job.id.toString())
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        getJobs()
                    } else {
                        Log.e("onRemoveJobListener", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }

    fun getPosts() {
        viewModelScope.launch {
            postRepository.getMyPosts()
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        result.data?.let { _postsData.postValue(it) }
                    } else {
                        Log.e("getMyPosts", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }

    fun onRemovePostClicked(post: Post) {
        viewModelScope.launch {
            postRepository.removePost(post.id.toString())
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        getPosts()
                    } else {
                        Log.e("onRemovePostListener", result.error?.statusMessage.orEmpty())
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
        val items: MutableList<Post> = postsData.value?.toMutableList() ?: mutableListOf()
        val index = items.indexOfFirst { it.id == post.id }
        items[index] = post
        _postsData.postValue(items)
    }
}