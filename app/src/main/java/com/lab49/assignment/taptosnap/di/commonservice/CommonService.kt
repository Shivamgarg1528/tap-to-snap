package com.lab49.assignment.taptosnap.di.commonservice

import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.data.repo.SnapRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonService {

    @Singleton
    @Binds
    abstract fun provideSnapRepo(snapRepoImpl: SnapRepoImpl): SnapRepo
}