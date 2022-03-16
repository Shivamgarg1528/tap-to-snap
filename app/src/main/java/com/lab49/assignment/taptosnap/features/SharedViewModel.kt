package com.lab49.assignment.taptosnap.features

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.data.model.request.local.ItemImageModel
import com.lab49.assignment.taptosnap.data.model.request.local.STATE
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val snapRepo: SnapRepo) : ViewModel() {

    private val itemsList = mutableListOf<ItemImageModel>()
    private val _itemsListEvent = Channel<Resource<ItemsListResponse>>()
    val itemsListEvent = _itemsListEvent.receiveAsFlow()

    fun getItems() {
        snapRepo.getItems().onEach { _itemsListEvent.send(it) }.launchIn(viewModelScope)
    }

    fun cacheResponse(itemsListResponse: ItemsListResponse) {
        itemsList.clear()
        itemsListResponse.forEach { itemsList.add(ItemImageModel(it)) }
    }

    fun reset() {
        val items = itemsList.map { it.copy(state = STATE.NOT_STARTED, bitmap = null) }
        itemsList.clear()
        itemsList.addAll(items)
    }

    fun isItemsAvailable() = itemsList.isNotEmpty()

    fun tookFirstItem() = itemsList.first()

    fun uploadItem(request: ItemRequest) {
        snapRepo.uploadItem(request).onEach { Log.d("TAG", "uploadItem() called $it")}.launchIn(viewModelScope)
    }

    private val _messageQueue = Channel<String>()
    val messageQueue = _messageQueue.receiveAsFlow()

    fun postMessage(message: String) {
        viewModelScope.launch { _messageQueue.send(message) }
    }
}