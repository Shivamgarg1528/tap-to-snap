package com.lab49.assignment.taptosnap.features

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.model.ItemsList
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val snapRepo: SnapRepo) : ViewModel() {

    private val _itemsListEvent = Channel<Resource<ItemsList>>()
    val itemsListEvent = _itemsListEvent.receiveAsFlow()

    fun getItems() {
        snapRepo.getItems().onEach { _itemsListEvent.send(it) }.launchIn(viewModelScope)
    }
}