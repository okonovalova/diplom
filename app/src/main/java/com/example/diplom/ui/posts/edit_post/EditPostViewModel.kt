package com.example.diplom.ui.posts.edit_post

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
import com.example.diplom.domain.entity.Post
import com.example.diplom.ui.model.ImageData
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val postRepository: PostRepository,
    private val mediaRepository: MediaRepository

) : ViewModel() {

    private val _downloadedImage: MutableLiveData<ImageData> = MutableLiveData(ImageData())
    val downloadedImage: LiveData<ImageData>
        get() = _downloadedImage
    private val _sendCoords: MutableLiveData<Boolean> = MutableLiveData(false)
    val sendCoords: LiveData<Boolean>
        get() = _sendCoords

    private val _coords: MutableLiveData<Coords?> = MutableLiveData(null)
    val coords: LiveData<Coords?>
        get() = _coords


    private var id: String = ""
    private var content: String = ""
    private var link: String? = null

    val navigateToMainFragment = SingleLiveEvent<Unit>()

    private val _contentError: MutableLiveData<Boolean> = MutableLiveData()
    val contentError: LiveData<Boolean>
        get() = _contentError

    fun onInitView(post: Post) {
        bottomMenuListener.showBottomMenu.postValue(false)
        id = post.id.toString()
        content = post.content
        link = post.link
    }

    fun onDoneButtonClicked(post: Post) {
        viewModelScope.launch {
            id = post.id.toString()
            val imageUri = downloadedImage.value?.uri
            val coords = coords.value
            if (imageUri != null) {
                mediaRepository.downloadImage(imageUri)
                    .flatMapConcat { url ->
                        postRepository.editPost(id, content, link, url.data, coords)
                    }
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {

                            navigateToMainFragment.postValue(Unit)
                        } else {
                            Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                        }
                    }
            } else {
                postRepository.editPost(id, content, link, downloadedImage.value?.url, coords)
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            navigateToMainFragment.postValue(Unit)
                        } else {
                            Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                        }
                    }
            }
        }
    }

    fun onChangeContent(login: String) {
        this.content = login
        _contentError.postValue(login.isBlank())
    }

    fun onNewPictureSet(uri: Uri) {
        _downloadedImage.postValue(ImageData(uri = uri))
    }

    fun setImageFromPost(url: String) {
        _downloadedImage.postValue(ImageData(url = url))
    }

    fun onCancelMedia() {
        _downloadedImage.postValue(ImageData())
    }

    fun onSendCoordsChecked(isChecked: Boolean) {
        _sendCoords.postValue(isChecked)
    }

    fun setCoords(lan: Double, lat: Double) {
        _coords.postValue(Coords(String.format("%.6f", lan), String.format("%.6f", lat)))
    }
}
