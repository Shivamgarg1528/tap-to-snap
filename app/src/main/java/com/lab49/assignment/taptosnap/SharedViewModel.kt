package com.lab49.assignment.taptosnap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _messageQueue = MutableSharedFlow<String>()
    val messageQueue = _messageQueue.asSharedFlow()

    /**
     * this method being used to display different notifications to user via Snackbar for now
     *
     * @param message
     */
    fun postMessage(message: String) {
        viewModelScope.launch { _messageQueue.emit(message) }
    }

    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("TTS", "log() called with: message = $message")
        }
    }
}