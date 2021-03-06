package com.lab49.assignment.taptosnap.data.model.request.local

import android.graphics.Bitmap
import com.lab49.assignment.taptosnap.util.Constants

data class ItemWrapper(
    val itemName: String,
    var state: Int = Constants.STATE.NOT_STARTED,
    var bitmap: Bitmap? = null,
) {
    fun canTap() = state == Constants.STATE.FAILED || state == Constants.STATE.NOT_STARTED
}