package com.lab49.assignment.taptosnap.di.network

import android.content.Context
import com.lab49.assignment.taptosnap.data.repo.server.AppService
import com.lab49.assignment.taptosnap.util.NoConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val interceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(NoConnectionInterceptor(context))
            .build()
        return Retrofit.Builder()
            .baseUrl("https://taptosnap.nonprod.kube.lab49cloud.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(retrofit: Retrofit): AppService {
        return retrofit.create(AppService::class.java)
    }
}