package com.lab49.assignment.taptosnap.di.commonservice

import com.lab49.assignment.taptosnap.data.repo.SnapRepo
import com.lab49.assignment.taptosnap.data.repo.SnapRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class CommonService {

    @ViewModelScoped
    @Binds
    abstract fun provideSnapRepo(snapRepoImpl: SnapRepoImpl): SnapRepo
}