package com.example.aqua_phoenix.altarixtestapplication.http

import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceInformationApi {
    @GET("json")
    fun getPlaceInformation(
        @Query("placeid") placeid: String,
        @Query("fields") fields: String,
        @Query("key") string: String
    ): Call<PlaceInfo>
}