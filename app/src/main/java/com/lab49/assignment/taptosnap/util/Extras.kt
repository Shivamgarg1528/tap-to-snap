package com.lab49.assignment.taptosnap.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import com.lab49.assignment.taptosnap.util.Constants.SWW
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

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

fun View.topMargin(margin: Int) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin = margin
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

val Int.toDp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Convert seconds to date ui format
 *
 * @param seconds
 * @return
 */
fun convertSecondsToDateUiFormat(seconds: Long): String {
    val hour = TimeUnit.SECONDS.toHours(seconds) % 24
    val minute = TimeUnit.SECONDS.toMinutes(seconds) % 60
    val second = TimeUnit.SECONDS.toSeconds(seconds) % 60
    return String.format("%02d:%02d:%02d", hour, minute, second)
}