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
    private val stateHandle: SavedStateHandle,
    private val snapRepo: SnapRepo,
) : ViewModel() {

    private var items: String = stateHandle.get<String>(Constants.KEY.ITEMS).orEmpty()
    private val _events = MutableStateFlow<Events>(Events.Empty)
    val events = _events.asStateFlow()

    /**
     * this function will fetch the items iff not available
     *
     */
    fun getItems() {
        if (items.isNotEmpty()) {
            _events.value = Events.Success(items = items)
            return
        }
        snapRepo.getItems().onEach { eachEvent ->
            when (eachEvent) {
                is Resource.Loading -> {
                    _events.value = Events.Loading
                }
                is Resource.Success -> {
                    if (eachEvent.result.isEmpty()) {
                        _events.value = Events.NoItemsFound
                    } else {
                        eachEvent.result.joinToString { it.name }.also {
                            items = it
                            stateHandle.set(Constants.KEY.ITEMS, items)
                            _events.value = Events.Success(items = items)
                        }
                    }
                }
                is Resource.Failure -> {
                    _events.value = Events.Failed(eachEvent.throwable)
                }
            }
        }.launchIn(viewModelScope)
    }

    sealed class Events {
        object Empty : Events()
        object Loading : Events()
        object NoItemsFound : Events()
        data class Failed(val exception: Throwable) : Events()
        data class Success(val items: String) : Events()
    }
}