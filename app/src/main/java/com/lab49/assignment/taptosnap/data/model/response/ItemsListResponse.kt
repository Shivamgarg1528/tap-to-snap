package com.lab49.assignment.taptosnap.data.model.response

import androidx.annotation.Keep

@Keep
class ItemsListResponse : ArrayList<ItemsListResponse.Item>() {
    @Keep
    data class Item(val name: String)
}