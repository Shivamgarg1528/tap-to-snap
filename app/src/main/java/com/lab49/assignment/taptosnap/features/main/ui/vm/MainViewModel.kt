package com.lab49.assignment.taptosnap.features.main.ui.vm

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
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

    private val _events = Channel<Events>(capacity = Channel.UNLIMITED)
    val events = _events.receiveAsFlow()

    init {
        if (items.isEmpty()) {
            sendEvent(Events.Empty)
        } else {
            itemsList = items.split(",")
                .map { ItemWrapper(it) }
                .toMutableList()
            sendEvent(Events.Items(itemsList))
            startTimer()
        }
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
        sendEvent(Events.Items(itemsList))
        startTimer()
    }

    /**
     * Exit from game
     *
     */
    fun exit() {
        sendEvent(Events.Exit)
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
            sendEvent(Events.Message(Constants.FTG))
        } else {
            val request = ItemRequest(lastTappedItem.itemName, bitmap.toBase64String())
            postImage(request = request)
        }
    }

    /**
     * this function will try to upload the image for verification
     *
     * @param request
     */
    private fun postImage(request: ItemRequest) {
        snapRepo.uploadItem(request)
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        updateState(
                            name = request.imageLabel,
                            state = Constants.STATE.RUNNING
                        )
                    }
                    is Resource.Failure -> {
                        updateState(
                            name = request.imageLabel,
                            state = Constants.STATE.FAILED
                        )
                    }
                    is Resource.Success -> {
                        var state = Constants.STATE.FAILED
                        if (it.result.matched) {
                            state = Constants.STATE.SUCCESS
                        }
                        updateState(
                            name = request.imageLabel,
                            state = state,
                        )
                    }
                }
                sendEvent(Events.Items(itemsList))
            }.launchIn(viewModelScope)
    }

    /**
     * Send event to ui for different actions
     *
     * @param event
     */
    private fun sendEvent(event: Events) {
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
                _events.send(Events.Timer(time = convertSecondsToDateUiFormat(timerInSeconds)))
                timerInSeconds -= 1
                delay(1000)
            }
            if (allSnapCaptured) {
                _events.send(Events.Won)
            } else if (timerInSeconds <= 0) {
                _events.send(Events.Timer(time = "00:00:00"))
                _events.send(Events.Lost)
            }
        }
    }

    sealed class Events {
        object Exit : Events()
        object Won : Events()
        object Lost : Events()
        object Empty : Events()
        data class Message(val message: String) : Events()
        data class Timer(val time: String) : Events()
        data class Items(val items: List<ItemWrapper>) : Events()
    }
}