package com.lab49.assignment.taptosnap.features

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.data.model.request.local.ItemWrapper
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(private val snapRepo: SnapRepo) : ViewModel() {
    private var timerJob: Job? = null
    var tappedItem: ItemWrapper? = null

    private var itemsList = mutableListOf<ItemWrapper>()
    private val _itemsListEvent = Channel<Resource<ItemsListResponse>>()
    val itemsListEvent = _itemsListEvent.receiveAsFlow()

    private val _events = Channel<Event>(capacity = UNLIMITED)
    val events = _events.receiveAsFlow()

    private val allSnapCaptured: Boolean
        get() = itemsList.size == itemsList.count { it.state == Constants.STATE.SUCCESS }

    private val _messageQueue = Channel<String>()
    val messageQueue = _messageQueue.receiveAsFlow()

    fun cachedItems(response: ItemsListResponse) {
        itemsList.clear()
        itemsList.addAll(response.map { ItemWrapper(it) })
    }

    /**
     * this function will fetch the items iff not available
     *
     */
    fun getItems() {
        if (!areItemsAvailable()) {
            snapRepo.getItems().onEach { _itemsListEvent.send(it) }.launchIn(viewModelScope)
        }
    }

    /**
     * Post items so that can be updated on UI
     *
     */
    fun postItemsOnUi() {
        sendEvent(Event.DataList(itemsList))
    }

    /**
     * Restart the game
     *
     */
    fun restartGame() {
        val newList = itemsList.toMutableList()
        newList.forEachIndexed { index, wrapper ->
            newList[index] = wrapper.copy(state = Constants.STATE.NOT_STARTED, bitmap = null)
        }
        itemsList = newList
        postItemsOnUi()
        startTimer()
    }

    /**
     * check if we have items
     *
     */
    fun areItemsAvailable() = itemsList.isNotEmpty()

    /**
     * this function will try to upload the image for verification
     *
     * @param request
     */
    fun postImage(request: ItemRequest) {
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
                postItemsOnUi()
            }.launchIn(viewModelScope)
    }

    /**
     * this method being used to display different notifications to user via Snackbar for now
     *
     * @param message
     */
    fun postMessage(message: String) {
        viewModelScope.launch { _messageQueue.send(message) }
    }

    /**
     * Update image will cache the captured image so can be used on different places
     *
     * @param name
     * @param bitmap
     * @return
     */
    fun updateImage(name: String, bitmap: Bitmap): Boolean {
        itemsList.forEach { wrapper ->
            if (wrapper.item.name == name) {
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
    fun startTimer() {
        if (timerJob?.isActive == true) {
            // don't restart the timer is already running
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
            cancel()
        }
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
            if (wrapper.item.name == name) {
                newList[index] = wrapper.copy(state = state)
            }
        }
        itemsList = newList
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
     * Convert seconds to date ui format
     *
     * @param seconds
     * @return
     */
    private fun convertSecondsToDateUiFormat(seconds: Long): String {
        val hour = TimeUnit.SECONDS.toHours(seconds) % 24
        val minute = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val second = TimeUnit.SECONDS.toSeconds(seconds) % 60
        return String.format("%02d:%02d:%02d", hour, minute, second)
    }

    sealed class Event {
        data class DataList(val items: List<ItemWrapper>) : Event()
        data class Timer(val time: String) : Event()
        object Won : Event()
        object Lost : Event()
    }
}