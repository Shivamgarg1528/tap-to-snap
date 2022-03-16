package com.lab49.assignment.taptosnap.data.repo.server

import com.lab49.assignment.taptosnap.data.model.request.ItemRequest
import com.lab49.assignment.taptosnap.data.model.response.ItemPostResponse
import com.lab49.assignment.taptosnap.data.model.response.ItemsListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AppService {
    @GET("item/list")
    suspend fun getItems(): Response<ItemsListResponse>

    @POST("item/image")
    suspend fun uploadItem(@Body request: ItemRequest): Response<ItemPostResponse>
}