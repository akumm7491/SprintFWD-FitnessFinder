package com.test.fitnessstudios.data.sources.remote.studio.api

import com.test.fitnessstudios.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object YelpNetwork {
    private val YELP_BASE_URL = "https://api.yelp.com/v3/"

    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(YELP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(YelpAPI::class.java)
    }

    // Intercept requests and add authorization header to the request.
    private val okHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(
            Interceptor { chain ->
                val builder = chain.request().newBuilder()
                builder.header("accept", "application/json")
                builder.header("Authorization", "Bearer ${BuildConfig.YELP_API_KEY}")
                return@Interceptor chain.proceed(builder.build())
            }
        )
    }.build()
}