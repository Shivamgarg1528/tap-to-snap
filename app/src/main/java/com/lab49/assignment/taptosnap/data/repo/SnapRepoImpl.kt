package com.lab49.assignment.taptosnap.data.repo

import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.data.model.response.ItemPostResponse
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import com.lab49.assignment.taptosnap.data.repo.server.AppService
import com.lab49.assignment.taptosnap.util.Resource
import com.lab49.assignment.taptosnap.util.networkCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SnapRepoImpl @Inject constructor(private val apiService: AppService) : SnapRepo {

    override fun getItems(): Flow<Resource<ItemsListResponse>> {
        return networkCall { apiService.getItems() }
    }

    override fun uploadItem(request: ItemRequest): Flow<Resource<ItemPostResponse>> {
        return networkCall { apiService.uploadItem(request) }
    }
}