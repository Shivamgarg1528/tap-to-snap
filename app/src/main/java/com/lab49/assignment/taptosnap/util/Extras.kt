package com.lab49.assignment.taptosnap.util

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Base64
import androidx.annotation.Keep
import com.lab49.assignment.taptosnap.util.Constants.SWW
import java.io.ByteArrayOutputStream

@Keep
sealed class Resource<in T> {
    class Loading<T> : Resource<T>()
    class Success<T>(val result: T) : Resource<T>()
    class Failure<T>(val throwable: Throwable) : Resource<T>()
}

internal fun Throwable.getMessageForUi(): String {
    if (this is NoConnectionInterceptor.NoConnectivityException) {
        return message
    }
    return SWW
}

fun Bitmap.toBase64String(): String {
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.JPEG, 100, this)
        return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
    }
}

val Int.toDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
