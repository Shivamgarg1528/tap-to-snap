package com.lab49.assignment.taptosnap.data.repo

import com.lab49.assignment.taptosnap.data.model.ItemRequest
import com.lab49.assignment.taptosnap.data.model.ItemsList
import com.lab49.assignment.taptosnap.data.repo.server.AppService
import com.lab49.assignment.taptosnap.util.Resource
import com.lab49.assignment.taptosnap.util.networkCall
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SnapRepoImpl @Inject constructor(private val apiService: AppService) : SnapRepo {

    override fun getItems(): Flow<Resource<ItemsList>> {
        return networkCall { apiService.getItems() }
    }

    override fun uploadItem(request: ItemRequest): Flow<Resource<ItemsList>> {
        return networkCall { apiService.uploadItem(request) }
    }
}