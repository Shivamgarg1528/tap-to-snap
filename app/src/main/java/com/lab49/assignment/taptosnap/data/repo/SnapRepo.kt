package com.lab49.assignment.taptosnap.data.repo

import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.data.model.response.ItemPostResponse
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.util.Resource
import kotlinx.coroutines.flow.Flow

interface SnapRepo {
    fun getItems(): Flow<Resource<ItemsListResponse>>
    fun uploadItem(request: ItemRequest): Flow<Resource<ItemPostResponse>>
}