package com.lab49.assignment.taptosnap.data.model.response

import com.google.gson.annotations.SerializedName

class ItemsListResponse : ArrayList<ItemsListResponse.Item>() {

    data class Item(
        @SerializedName("name")
        val name: String,
    )
}