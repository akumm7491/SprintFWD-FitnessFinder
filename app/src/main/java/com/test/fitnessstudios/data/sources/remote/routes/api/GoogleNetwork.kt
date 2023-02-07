package com.test.fitnessstudios.data.sources.remote.routes.api

import com.test.fitnessstudios.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GoogleNetwork {
    private val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(GoogleAPI::class.java)
    }

    // Intercept requests and add authorization header to the request.
    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("accept", "application/json")
                builder.header("Authorization", "Bearer ${BuildConfig.MAPS_API_KEY}")
                return@Interceptor chain.proceed(builder.build())
            }
        )
    }.build()
}