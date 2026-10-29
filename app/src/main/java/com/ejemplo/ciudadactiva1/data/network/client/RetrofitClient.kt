package com.ejemplo.ciudadactiva1.data.network.client

import android.util.Log
import com.ejemplo.ciudadactiva1.data.network.SessionManager
import com.ejemplo.ciudadactiva1.data.network.api.ApiService
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://ciudadactiva1.onrender.com/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = SessionManager.authToken
        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrBlank()) {
            val cleanToken = token.trim().removeSurrounding("\"").trim()
            val asciiToken = cleanToken.filter { it.code in 32..126 }

            if (asciiToken.isNotBlank()) {
                val headerValue = if (asciiToken.startsWith("Bearer ", ignoreCase = true)) asciiToken else "Bearer $asciiToken"
                requestBuilder.addHeader("Authorization", headerValue)
                Log.d("RetrofitClient", "Header Authorization adjuntado: $headerValue")
            }
        } else {
            Log.w("RetrofitClient", "Petición enviada sin Token (SessionManager.authToken está vacío o nulo)")
        }

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
