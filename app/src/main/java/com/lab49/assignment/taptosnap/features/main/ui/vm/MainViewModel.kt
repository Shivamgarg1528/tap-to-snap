package com.lab49.assignment.taptosnap.features.main.ui.vm

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.model.request.ItemPostRequest
import com.lab49.assignment.taptosnap.data.model.request.local.ItemWrapper
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.Resource
import com.lab49.assignment.taptosnap.util.convertSecondsToDateUiFormat
import com.lab49.assignment.taptosnap.util.toBase64String
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val snapRepo: SnapRepo,
) : ViewModel() {

    private var items: String = savedStateHandle.get<String>(Constants.KEY.ITEMS).orEmpty()
    private var itemsList = mutableListOf<ItemWrapper>()

    private var timerJob: Job? = null
    private var tappedItem: ItemWrapper? = null

    private val allSnapCaptured: Boolean
        get() = itemsList.size > 0 && itemsList.size == itemsList.count { it.state == Constants.STATE.SUCCESS }

    private val _events = Channel<Event>(capacity = Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    init {
        if (items.isEmpty()) {
            sendEvent(Event.Empty)
        } else {
            itemsList = items.split(",")
                .map { ItemWrapper(it) }
                .toMutableList()
            sendEvent(Event.Items(itemsList))
            startTimer()
        }
    }


    /**
     * Check recreation
     *
     */
    fun checkRecreation() {
        if (timerJob?.isActive == false) {
            sendEvent(Event.Timer(time = "00:00:00"))
        }
        sendEvent(Event.Items(itemsList))
    }

    /**
     * Restart the game
     *
     */
    fun restart() {
        val newList = itemsList.toMutableList()
        newList.forEachIndexed { index, wrapper ->
            newList[index] = wrapper.copy(state = Constants.STATE.NOT_STARTED, bitmap = null)
        }
        itemsList = newList
        sendEvent(Event.Items(itemsList))
        startTimer()
    }

    /**
     * Exit from game
     *
     */
    fun exit() {
        sendEvent(Event.Exit)
    }

    /**
     * Cache tapped item for mapping when image captured
     *
     * @param tappedItem
     */
    fun cacheTappedItem(tappedItem: ItemWrapper?) {
        this.tappedItem = tappedItem
    }

    /**
     * this will call when an images has been captured by user
     *
     * @param bitmap
     */
    fun captured(bitmap: Bitmap?) {
        val lastTappedItem = tappedItem
        if (bitmap == null
            || lastTappedItem == null
            || !updateImage(lastTappedItem.itemName, bitmap)
        ) {
            sendEvent(Event.Message(Constants.FTG))
        } else {
            val request = ItemPostRequest(lastTappedItem.itemName, bitmap.toBase64String())
            postImage(itemPostRequest = request)
        }
    }

    /**
     * this function will try to upload the image for verification
     *
     * @param itemPostRequest
     */
    fun postImage(itemPostRequest: ItemPostRequest) {
        snapRepo.uploadItem(itemPostRequest)
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        updateState(
                            name = itemPostRequest.imageLabel,
                            state = Constants.STATE.RUNNING
                        )
                    }
                    is Resource.Failure -> {
                        updateState(
                            name = itemPostRequest.imageLabel,
                            state = Constants.STATE.FAILED
                        )
                    }
                    is Resource.Success -> {
                        var state = Constants.STATE.FAILED
                        if (it.result.matched) {
                            state = Constants.STATE.SUCCESS
                        }
                        updateState(
                            name = itemPostRequest.imageLabel,
                            state = state,
                        )
                    }
                }
                sendEvent(Event.Items(itemsList))
            }.launchIn(viewModelScope)
    }

    /**
     * Send event to ui for different actions
     *
     * @param event
     */
    private fun sendEvent(event: Event) {
        viewModelScope.launch { _events.send(event) }
    }

    /**
     * Update states to reflect on UI
     *
     * @param name
     * @param state
     */
    private fun updateState(
        name: String,
        state: Int,
    ) {
        val newList = itemsList.toMutableList()
        newList.forEachIndexed { index, wrapper ->
            if (wrapper.itemName == name) {
                newList[index] = wrapper.copy(state = state)
            }
        }
        itemsList = newList
    }


    /**
     * Update image will cache the captured image so can be used on different places
     *
     * @param name
     * @param bitmap
     * @return
     */
    private fun updateImage(
        name: String,
        bitmap: Bitmap,
    ): Boolean {
        itemsList.forEach { wrapper ->
            if (wrapper.itemName == name) {
                wrapper.bitmap = bitmap
                return true
            }
        }
        return false
    }

    /**
     * Start the timer iff not running
     *
     */
    private fun startTimer() {
        if (timerJob?.isActive == true) {
            // don't restart the timer iff already running
            return
        }
        timerJob = viewModelScope.launch {
            var timerInSeconds = Constants.MAX_TIMER_IN_SECONDS
            while (timerInSeconds > 0 && isActive && !allSnapCaptured) {
                _events.send(Event.Timer(time = convertSecondsToDateUiFormat(timerInSeconds)))
                timerInSeconds -= 1
                delay(1000)
            }
            if (allSnapCaptured) {
                _events.send(Event.Won)
            } else if (timerInSeconds <= 0) {
                _events.send(Event.Timer(time = "00:00:00"))
                _events.send(Event.Lost)
            }
        }
    }

    sealed class Event {
        object Exit : Event()
        object Won : Event()
        object Lost : Event()
        object Empty : Event()
        data class Message(val message: String) : Event()
        data class Timer(val time: String) : Event()
        data class Items(val items: List<ItemWrapper>) : Event()
    }
}