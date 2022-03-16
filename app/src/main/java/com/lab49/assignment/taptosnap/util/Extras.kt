package com.lab49.assignment.taptosnap.util

import android.os.SystemClock
import android.view.View
import androidx.annotation.Keep


@Keep
sealed class Resource<in T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val result: T) : Resource<T>()
    class Failure<T>(val throwable: Throwable) : Resource<T>()
}

fun View.clickWithDebounce(debounceTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < debounceTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

internal fun Throwable.getMessageForUi(): String {
    if (this is NoConnectionInterceptor.NoConnectivityException) {
        return message
    }
    return "Something went wrong!"
}

fun View.visible() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}