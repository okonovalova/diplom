package com.example.diplom.ui.events.events_feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.diplom.data.network.DataResult
import com.example.diplom.data.repository.EventRepository
import com.example.diplom.domain.entity.Attachment
import com.example.diplom.domain.entity.Event
import com.example.diplom.domain.entity.STATE
import com.example.diplom.ui.utils.BottomMenuListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventsFeedViewModel @Inject constructor(
    private val bottomMenuListener: BottomMenuListener,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _eventsData: MutableLiveData<List<Event>> = MutableLiveData()
    val eventsData: LiveData<List<Event>>
        get() = _eventsData

    private val _playingMediaEvent: MutableLiveData<Event?> = MutableLiveData()
    val playingMediaEvent: LiveData<Event?>
        get() = _playingMediaEvent

    fun onInitView() {
        bottomMenuListener.showBottomMenu.postValue(true)
    }

    fun onLikeListener(event: Event) {
        viewModelScope.launch {
            if (event.likedByMe) {
                eventRepository.dislikeEvent(event.id)
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updateEvent(it) }
                        } else {
                            Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            } else {
                eventRepository.likeEvent(event.id)
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updateEvent(it) }
                        } else {
                            Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            }
        }
    }

    fun onParticipateListener(event: Event) {
        viewModelScope.launch {
            if (event.participatedByMe) {
                eventRepository.deleteParticipation(event.id)
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updateEvent(it) }
                        } else {
                            Log.e("onParticipateListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            } else {
                eventRepository.addParticipation(event.id)
                    .collect { result ->
                        if (result.status == DataResult.Status.SUCCESS) {
                            result.data?.let { updateEvent(it) }
                        } else {
                            Log.e("onLikeListener", result.error?.statusMessage.orEmpty())
                        }
                    }
            }
        }
    }

    fun getEvents() {
        viewModelScope.launch {
            eventRepository.getEvents()
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        result.data?.let { _eventsData.postValue(it) }
                    } else {
                        Log.e("getEvents", result.error?.statusMessage.orEmpty())
                    }
                }

        }
    }

    fun onMediaClicked(event: Event) {
        val state = when {
            (event.id == playingMediaEvent.value?.id) && (event.attachment?.state == STATE.PLAY) -> STATE.PAUSE
            (playingMediaEvent.value == null) || (event.attachment?.state == STATE.PAUSE) || (event.id != playingMediaEvent.value?.id) -> STATE.PLAY
            else -> STATE.NOT_PLAY
        }
        val attachment = event.attachment?.let { Attachment(it.type, event.attachment.url, state) }
        val newEvent = event.copy(attachment = attachment)
        if (attachment?.state != STATE.NOT_PLAY) _playingMediaEvent.postValue(newEvent)
        else _playingMediaEvent.postValue(null)
        updateEvent(newEvent)
    }


    fun onFinishMedia() {
        val attachment = _playingMediaEvent.value?.attachment?.let { Attachment(it.type, it.url, STATE.NOT_PLAY) }
        val newEvent = _playingMediaEvent.value?.copy(attachment = attachment)
        if (newEvent != null) {
            updateEvent(newEvent)
        }
        _playingMediaEvent.postValue(null)
    }


    private fun updateEvent(event: Event) {
        val items: MutableList<Event> = eventsData.value
            ?.map { updatedEvent ->
                if (updatedEvent.id == event.id) {
                    event
                } else {
                    val attachment = updatedEvent.attachment?.let { Attachment(it.type, it.url, STATE.NOT_PLAY) }
                    updatedEvent.copy(attachment = attachment)
                }
            }
            ?.toMutableList() ?: mutableListOf()
        _eventsData.postValue(items)


    }

    fun onRemoveClicked(event: Event) {
        viewModelScope.launch {
            eventRepository.removeEvent(event.id.toString())
                .collect { result ->
                    if (result.status == DataResult.Status.SUCCESS) {
                        getEvents()
                    } else {
                        Log.e("onDeleteListener", result.error?.statusMessage.orEmpty())
                    }
                }
        }
    }
}