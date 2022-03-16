package com.lab49.assignment.taptosnap.data.repo.server

import com.lab49.assignment.taptosnap.data.model.ItemRequest
import com.lab49.assignment.taptosnap.data.model.ItemsList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AppService {
    @GET("item/list")
    suspend fun getItems(): Response<ItemsList>

    @POST("item/image")
    suspend fun uploadItem(@Body request: ItemRequest): Response<ItemsList>
}