package com.example.diplom.ui.posts.add_post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.MediaRepository
import com.example.diplom.data.repository.PostRepository
import com.example.diplom.domain.entity.Coords
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPostViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository
) : ViewModel() {
    private val _downloadedImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val downloadedImage: LiveData<Uri?>
        get() = _downloadedImage
    private val _sendCoords: MutableLiveData<Boolean> = MutableLiveData(false)
    val sendCoords: LiveData<Boolean>
        get() = _sendCoords

    private val _coords: MutableLiveData<Coords?> = MutableLiveData(null)
    val coords: LiveData<Coords?>
        get() = _coords

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(false)
    }

    private var content: String = ""
    private var link: String? = null
    val navigateToMainFragment = SingleLiveEvent<Unit>()

    private val _contentError: MutableLiveData<Boolean> = MutableLiveData()
    val contentError: LiveData<Boolean>
        get() = _contentError

    fun onDoneButtonClicked() {
        if (content.isBlank()) return
        viewModelScope.launch {
            val imageUri = downloadedImage.value
            val coords = coords.value
            if (imageUri != null) {
                val url = mediaRepository.downloadImage(imageUri)
                val result = postRepository.createPost(content, link, url.data, coords)
                if (result.status == DataResult.Status.SUCCESS) {
                    navigateToMainFragment.postValue(Unit)
                } else {
                    Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                }
            } else {
                val result = postRepository.createPost(content, link, null, coords)
                if (result.status == DataResult.Status.SUCCESS) {
                    navigateToMainFragment.postValue(Unit)
                } else {
                    Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                }
            }
        }
    }

    fun onChangeContent(login: String) {
        this.content = login
        _contentError.postValue(login.isBlank())
    }

    fun onNewPictureSet(uri: Uri) {
        _downloadedImage.postValue(uri)
    }

    fun onCancelMedia() {
        _downloadedImage.postValue(null)
    }

    fun onSendCoordsChecked() {
        val isChecked = sendCoords.value ?: false
        _sendCoords.postValue(!isChecked)
    }

    fun setCoords(lan: Double, lat: Double) {
        _coords.postValue(Coords(String.format("%.6f", lan), String.format("%.6f", lat)))
    }
}