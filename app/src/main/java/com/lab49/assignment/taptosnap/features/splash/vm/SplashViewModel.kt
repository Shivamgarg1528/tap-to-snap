package com.lab49.assignment.taptosnap.features.splash.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.util.Constants
import com.lab49.assignment.taptosnap.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val snapRepo: SnapRepo,
) : ViewModel() {

    private var items: String = savedStateHandle.get<String>(Constants.KEY.ITEMS).orEmpty()
    private val _events = MutableStateFlow<Event>(Event.NoOperation)
    val events = _events.asStateFlow()

    /**
     * this function will fetch the items iff not available
     *
     */
    fun getItems() {
        if (items.isNotEmpty()) {
            _events.value = Event.Success(items = items)
            return
        }
        snapRepo.getItems().onEach { eachEvent ->
            when (eachEvent) {
                is Resource.Loading -> {
                    _events.value = Event.Loading
                }
                is Resource.Success -> {
                    if (eachEvent.result.isEmpty()) {
                        _events.value = Event.Empty
                    } else {
                        eachEvent.result.joinToString { it.name }.also {
                            items = it
                            savedStateHandle.set(Constants.KEY.ITEMS, items)
                            _events.value = Event.Success(items = items)
                        }
                    }
                }
                is Resource.Failure -> {
                    _events.value = Event.Failed(eachEvent.throwable)
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class Event {
        object NoOperation : Event()
        object Empty : Event()
        object Loading : Event()
        data class Failed(val exception: Throwable) : Event()
        data class Success(val items: String) : Event()
    }
}