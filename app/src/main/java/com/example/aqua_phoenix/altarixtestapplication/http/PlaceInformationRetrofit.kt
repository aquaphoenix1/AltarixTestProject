package com.example.aqua_phoenix.altarixtestapplication.http

import android.util.Log
import com.example.aqua_phoenix.altarixtestapplication.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object PlaceInformationRetrofit {

    var BASE_URL_GET_PLACE_ADDRESS = "maps.googleapis.com/maps/api/place/"
    get() {
        return "${if (USE_HTTPS_GET_PLACE_ADDRESS) "https" else "http"}://$field"
    }
    const val USE_HTTPS_GET_PLACE_ADDRESS = true


    lateinit var placeInformationApi: PlaceInformationApi

    fun init() {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()

        val gson = GsonBuilder()
            .setPrettyPrinting()
            .create()

        val gsonConverterFactory = GsonConverterFactory.create(gson)

        //val apiKey= "@string/strings_api_key"

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_GET_PLACE_ADDRESS)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

        placeInformationApi = retrofit.create(PlaceInformationApi::class.java)
    }
}