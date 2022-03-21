package com.lab49.assignment.taptosnap.data.model.response

import androidx.annotation.Keep

@Keep
data class ItemPostResponse(
    val matched: Boolean,
    val imageLabel: String,
)