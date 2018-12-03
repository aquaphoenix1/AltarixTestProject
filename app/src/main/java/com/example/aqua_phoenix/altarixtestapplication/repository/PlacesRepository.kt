package com.example.aqua_phoenix.altarixtestapplication.repository

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.example.aqua_phoenix.altarixtestapplication.App
import com.example.aqua_phoenix.altarixtestapplication.R
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInformation
import com.example.aqua_phoenix.altarixtestapplication.entites.getDefaultPlace
import com.example.aqua_phoenix.altarixtestapplication.http.PlaceInformationRetrofit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


object PlacesRepository {
    private val placeInfoDAO by lazy {
        App.placeInfoDB.getPlaceInfoDB()
    }

    private val liveData = MutableLiveData<PlaceInfo>()

    fun getPlaceInfo(placeInfoId: String, context: Context): MutableLiveData<PlaceInfo> {
        refreshPlaceInfo(placeInfoId, context)
        return liveData//placeInfoDAO.load(placeInfoId)
    }

    private fun refreshPlaceInfo(placeInfoId: String, context: Context) {
        GlobalScope.launch {
            val hasPlaceInfo = placeInfoDAO.hasPlaceInfo(placeInfoId) > 0

            if (!hasPlaceInfo) {
                PlaceInformationRetrofit.placeInformationApi.getPlaceInformation(
                    placeInfoId,
                    "name,rating,formatted_phone_number,photo,type,url,address_component,geometry",
                    context.getString(R.string.strings_api_key)
                ).enqueue(object : Callback<PlaceInformation> {
                    override fun onFailure(call: Call<PlaceInformation>, t: Throwable) {
                        liveData.postValue(getDefaultPlace(context))
                    }

                    override fun onResponse(call: Call<PlaceInformation>, response: Response<PlaceInformation>) {
                        if (response.isSuccessful) {
                            response.body()?.let { placeInformation ->
                                placeInformation.result?.let { result ->
                                    GlobalScope.launch {
                                        val types: String = result.types?.joinToString(separator = " ")

                                        var address = ""
                                        result.address_components.forEach {
                                            address += it.short_name + " "
                                        }

                                        if (address.isNotEmpty()) {
                                            address = address.substring(0, address.length - 1)
                                        }

                                        val place = PlaceInfo(
                                            placeInfoId,
                                            address = if (address.isNotEmpty()) address else context.getString(R.string.defaultAddress),
                                            name = if (result.name.isNotEmpty()) result.name else context.getString(R.string.defaultName),
                                            types = if (types.isNotEmpty()) types else context.getString(R.string.defaultTypes),
                                            url = if (result.url.isNotEmpty()) result.url else context.getString(R.string.defaultURL),
                                            raiting = if (result.rating.toString().isNotEmpty()) result.rating.toString() else context.getString(
                                                R.string.defaultRating
                                            ),
                                            phone = if (result.formatted_phone_number.isNotEmpty()) result.formatted_phone_number else context.getString(
                                                R.string.defaultPhone
                                            ),
                                            imagePath = if (result.photos != null && result.photos[0] != null && result.photos[0].photo_reference.isNotEmpty())
                                                "${PlaceInformationRetrofit.BASE_URL_GET_PLACE_ADDRESS}photo?maxwidth=400&photoreference=${result.photos[0].photo_reference}&key=${context.getString(
                                                    R.string.strings_api_key
                                                )}"
                                            else null,
                                            geometry = if(result.geometry != null && result.geometry.location != null) result.geometry.location.toString() else null
                                        )

                                        placeInfoDAO.save(
                                            place
                                        )

                                        liveData.postValue(place)
                                    }
                                } ?: run {
                                    liveData.postValue(getDefaultPlace(context))
                                }
                            } ?: run {
                                liveData.postValue(getDefaultPlace(context))
                            }
                        }

                    }
                })
            } else {
                liveData.postValue(placeInfoDAO.load(placeInfoId))
            }
        }
    }
}