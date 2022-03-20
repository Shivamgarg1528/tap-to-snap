package com.lab49.assignment.taptosnap.data.model.request.local

import android.graphics.Bitmap
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.util.Constants

data class ItemWrapper(
    val item: ItemsListResponse.Item,
    var state: Int = Constants.STATE.NOT_STARTED,
    var bitmap: Bitmap? = null,
) {
    fun canTap() = state == Constants.STATE.FAILED || state == Constants.STATE.NOT_STARTED
}