package com.example.aqua_phoenix.altarixtestapplication.http

import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInformation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceInformationApi {
    @GET("details/json")
    fun getPlaceInformation(
        @Query("placeid") placeid: String,
        @Query("fields") fields: String,
        @Query("key") string: String
    ): Call<PlaceInformation>
}