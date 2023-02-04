package com.test.fitnessstudios.di


import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.test.fitnessstudios.data.repositories.studio.StudioRepository
import com.test.fitnessstudios.data.sources.remote.StudioRemoteDataSource
import com.test.fitnessstudios.data.sources.remote.service.StudioService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

// @Module annotation which will make this class a module
// to inject dependency to other class within it's scope.
// @InstallIn(SingletonComponent::class) this will make
// this class to inject dependencies across the entire application.
@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideStudioService(): StudioService= StudioService()

    @Provides
    @Singleton
    fun provideStudioRemoteDataSource(): StudioRemoteDataSource=StudioRemoteDataSource(
        provideStudioService(),
        Dispatchers.IO
    )

    @Provides
    @Singleton
    fun provideStudioRepository():StudioRepository=StudioRepository(
        provideStudioRemoteDataSource(),
        Dispatchers.IO
    )

}