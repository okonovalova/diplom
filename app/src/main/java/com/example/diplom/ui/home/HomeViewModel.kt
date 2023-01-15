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

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    val errorMessage = SingleLiveEvent<String>()

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(true)
    }

    fun getInfo() {
        _isLoading.postValue(true)
        viewModelScope.launch {
            getUserInfo()
            getJobs()
            getPosts()
            _isLoading.postValue(false)
        }
    }

    private suspend fun getUserInfo() {
        val result = userRepository.getUserInfo()
        if (result.status == DataResult.Status.SUCCESS) {
            result.data?.let { _userData.postValue(it) }
        } else {
            errorMessage.postValue(result.error?.statusMessage.toString())
            Log.e("getUserInfo", result.error?.statusMessage.orEmpty())
        }
    }

    private suspend fun getJobs() {
        val result = jobRepository.getJobs()
        if (result.status == DataResult.Status.SUCCESS) {
            result.data?.let { _jobsData.postValue(it) }
        } else {
            errorMessage.postValue(result.error?.statusMessage.toString())
            Log.e("getJobs", result.error?.statusMessage.orEmpty())
        }
    }

    private suspend fun getPosts() {
        val result = postRepository.getMyPosts()
        if (result.status == DataResult.Status.SUCCESS) {
            result.data?.let { _postsData.postValue(it) }
        } else {
            errorMessage.postValue(result.error?.statusMessage.toString())
            Log.e("getMyPosts", result.error?.statusMessage.orEmpty())
        }
    }

    fun onRemoveJobClicked(job: Job) {
        viewModelScope.launch {
            val result = jobRepository.removeJob(job.id.toString())
            if (result.status == DataResult.Status.SUCCESS) {
                getJobs()
            } else {
                Log.e("onRemoveJobListener", result.error?.statusMessage.orEmpty())
            }
        }
    }

    fun onRemovePostClicked(post: Post) {
        viewModelScope.launch {
            val result = postRepository.removePost(post.id.toString())
            if (result.status == DataResult.Status.SUCCESS) {
                getPosts()
            } else {
                Log.e("onRemovePostListener", result.error?.statusMessage.orEmpty())
            }
        }
    }

    fun onLikeClicked(post: Post) {
        viewModelScope.launch {
            if (post.likedByMe) {
                val result = postRepository.dislikePost(post.id.toString())
                if (result.status == DataResult.Status.SUCCESS) {
                    result.data?.let { updatePost(it) }
                } else {
                    Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                }
            } else {
                val result = postRepository.likePost(post.id.toString())
                if (result.status == DataResult.Status.SUCCESS) {
                    result.data?.let { updatePost(it) }
                } else {
                    Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
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