package com.lab49.assignment.taptosnap

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    private val _messageQueue = Channel<String>()
    val messageQueue = _messageQueue.receiveAsFlow()

    /**
     * this method being used to display different notifications to user via Snackbar for now
     *
     * @param message
     */
    fun postMessage(message: String) {
        viewModelScope.launch { _messageQueue.send(message) }
    }

    fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("TTS", "log() called with: message = $message")
        }
    }
}