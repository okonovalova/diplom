package com.example.diplom.ui.events.edit_event

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.EventRepository
import com.example.diplom.data.repository.MediaRepository
import com.example.diplom.domain.entity.Coords
import com.example.diplom.domain.entity.Event
import com.example.diplom.ui.model.ImageData
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val eventRepository: EventRepository,
    private val mediaRepository: MediaRepository,
) : ViewModel() {

    private val _eventType: MutableLiveData<String> = MutableLiveData("OFFLINE")
    val eventType: LiveData<String>
        get() = _eventType

    private val _eventDateTime: MutableLiveData<String> = MutableLiveData("")
    val eventDateTime: LiveData<String>
        get() = _eventDateTime

    private val _downloadedImage: MutableLiveData<ImageData> = MutableLiveData(ImageData())
    val downloadedImage: LiveData<ImageData>
        get() = _downloadedImage

    private val _sendCoords: MutableLiveData<Boolean> = MutableLiveData(false)
    val sendCoords: LiveData<Boolean>
        get() = _sendCoords

    private val _coords: MutableLiveData<Coords?> = MutableLiveData(null)
    val coords: LiveData<Coords?>
        get() = _coords

    private val _contentError: MutableLiveData<Boolean> = MutableLiveData()
    val contentError: LiveData<Boolean>
        get() = _contentError

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var content: String = ""
    private var link: String? = null
    val navigateToMainFragment = SingleLiveEvent<Unit>()

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(false)
    }

    fun onDoneButtonClicked(event: Event) {
        if (content.isBlank()) return
        _isLoading.postValue(true)
        viewModelScope.launch {
            val id = event.id.toString()
            val imageUri = downloadedImage.value?.uri
            val coords = coords.value
            val eventType = eventType.value
            val dateTime = eventDateTime.value
            if (imageUri != null) {
                val url = mediaRepository.downloadImage(imageUri)
                val result = eventRepository.editEvent(id, content, link, url.data, coords, eventType.orEmpty(), dateTime.orEmpty())
                if (result.status == DataResult.Status.SUCCESS) {
                    navigateToMainFragment.postValue(Unit)
                } else {
                    Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                }
            } else {
                val result = eventRepository.editEvent(id, content, link, downloadedImage.value?.url, coords, eventType.orEmpty(), dateTime.orEmpty())
                if (result.status == DataResult.Status.SUCCESS) {
                    navigateToMainFragment.postValue(Unit)
                } else {
                    Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                }
            }
            _isLoading.postValue(false)

        }
    }

    fun onChangeContent(content: String) {
        this.content = content
        _contentError.postValue(content.isBlank())
    }

    fun onNewPictureSet(uri: Uri) {
        _downloadedImage.postValue(ImageData(uri = uri))
    }

    fun setImageFromEvent(url: String) {
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

    fun setEventType(type: String) {
        _eventType.postValue(type)
    }

    fun setEventDateTime(dateTime: String) {
        _eventDateTime.postValue(dateTime)
    }
}