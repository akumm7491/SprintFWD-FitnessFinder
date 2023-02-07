package com.test.fitnessstudios.di


import com.test.fitnessstudios.data.repositories.routes.RoutesRepository
import com.test.fitnessstudios.data.repositories.studio.StudioRepository
import com.test.fitnessstudios.data.sources.remote.routes.RouteRemoteDataSource
import com.test.fitnessstudios.data.sources.remote.routes.service.RouteService
import com.test.fitnessstudios.data.sources.remote.studio.StudioRemoteDataSource
import com.test.fitnessstudios.data.sources.remote.studio.service.StudioService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideStudioService(): StudioService = StudioService()

    @Provides
    @Singleton
    fun provideStudioRemoteDataSource(): StudioRemoteDataSource = StudioRemoteDataSource(
        provideStudioService(),
        Dispatchers.IO
    )

    @Provides
    @Singleton
    fun provideStudioRepository():StudioRepository=StudioRepository(
        provideStudioRemoteDataSource(),
        Dispatchers.IO
    )


    @Provides
    @Singleton
    fun provideRouteService(): RouteService = RouteService()

    @Provides
    @Singleton
    fun provideRouteRemoteDataSource(): RouteRemoteDataSource = RouteRemoteDataSource(
        provideRouteService(),
        Dispatchers.IO
    )

    @Provides
    @Singleton
    fun provideRouteRepository():RoutesRepository=RoutesRepository(
        provideRouteRemoteDataSource(),
        Dispatchers.IO
    )
}