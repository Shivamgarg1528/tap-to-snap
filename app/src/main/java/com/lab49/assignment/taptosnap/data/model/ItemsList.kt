package com.lab49.assignment.taptosnap.data.model

import com.google.gson.annotations.SerializedName

class ItemsList : ArrayList<ItemsList.ItemsResponseItem>() {

    data class ItemsResponseItem(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
    )
}