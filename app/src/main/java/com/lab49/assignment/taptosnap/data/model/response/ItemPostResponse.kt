package com.lab49.assignment.taptosnap.data.model.response

import com.google.gson.annotations.SerializedName

data class ItemPostResponse(
    @SerializedName("matched")
    val matched: Boolean,
    @SerializedName("imageLabel")
    val imageLabel: String,
)