package com.lab49.assignment.taptosnap.data.model.response

import com.google.gson.annotations.SerializedName

class ItemsListResponse : ArrayList<ItemsListResponse.ItemsResponseItem>() {

    data class ItemsResponseItem(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
    )
}