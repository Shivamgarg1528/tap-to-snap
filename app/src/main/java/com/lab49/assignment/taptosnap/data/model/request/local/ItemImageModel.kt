package com.lab49.assignment.taptosnap.data.model.request.local

import android.graphics.Bitmap
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse

data class ItemImageModel(
    val item: ItemsListResponse.ItemsResponseItem,
    val state: STATE = STATE.NOT_STARTED,
    val bitmap: Bitmap? = null,
)

enum class STATE {
    NOT_STARTED,
    RUNNING,
    SUCCESS,
    FAILED
}