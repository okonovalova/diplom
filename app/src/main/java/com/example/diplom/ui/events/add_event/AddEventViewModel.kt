package com.example.diplom.ui.events.add_event

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
import com.example.diplom.ui.utils.BottomMenuListener
import com.example.diplom.ui.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
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

    private val _downloadedImage: MutableLiveData<Uri?> = MutableLiveData(null)
    val downloadedImage: LiveData<Uri?>
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

    private var content: String = ""
    private var link: String? = null
    val navigateToMainFragment = SingleLiveEvent<Unit>()

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(false)
    }

    fun onDoneButtonClicked() {
        if (content.isBlank()) return
        viewModelScope.launch {
            val imageUri = downloadedImage.value
            val coords = coords.value
            val eventType = eventType.value
            val dateTime = eventDateTime.value
            if (imageUri != null) {
                mediaRepository.downloadImage(imageUri)
                    .flatMapConcat { url ->
                        eventRepository.createEvent(content, link, url.data, coords, eventType.orEmpty(), dateTime.orEmpty())
                    }
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            navigateToMainFragment.postValue(Unit)
                        } else {
                            Log.e("onDoneButtonClicked", result.error?.statusMessage.orEmpty())
                        }
                    }
            } else {
                eventRepository.createEvent(content, link, null, coords, eventType.orEmpty(), dateTime.orEmpty())
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

    fun setEventType(type: String) {
        _eventType.postValue(type)
    }

    fun setEventDateTime(dateTime: String) {
        _eventDateTime.postValue(dateTime)
    }
}