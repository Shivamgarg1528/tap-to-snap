package com.lab49.assignment.taptosnap.data.repo

import com.lab49.assignment.taptosnap.data.model.ItemRequest
import com.lab49.assignment.taptosnap.data.model.ItemsList
import com.lab49.assignment.taptosnap.util.Resource
import kotlinx.coroutines.flow.Flow

interface SnapRepo {
    fun getItems(): Flow<Resource<ItemsList>>
    fun uploadItem(request: ItemRequest): Flow<Resource<ItemsList>>
}