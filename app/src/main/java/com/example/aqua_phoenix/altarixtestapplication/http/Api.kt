package com.example.aqua_phoenix.altarixtestapplication.http

import android.util.Log
import com.example.aqua_phoenix.altarixtestapplication.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Api {
    val BASE_URL_GET_PLACE_ADDRESS = "maps.googleapis.com/maps/api/geocode/"
    val USE_HTTPS_GET_PLACE_ADDRESS = true

    val url = "${if (USE_HTTPS_GET_PLACE_ADDRESS) "https" else "http"}://${BASE_URL_GET_PLACE_ADDRESS}"

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

        var getUrl = url
        getUrl+="latlng=40,40&key=AIzaSyCmKmY5c5x5lvSMhmrCwHYSxhJvJz5G_Sk"
        Log.d("11111111111111111111111", getUrl)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/details/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

        placeInformationApi = retrofit.create(PlaceInformationApi::class.java)
    }
}