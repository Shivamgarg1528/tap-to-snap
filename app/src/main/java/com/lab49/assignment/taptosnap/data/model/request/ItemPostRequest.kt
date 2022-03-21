package com.lab49.assignment.taptosnap.data.model.request

import androidx.annotation.Keep

@Keep
data class ItemPostRequest(val imageLabel: String, val image: String)