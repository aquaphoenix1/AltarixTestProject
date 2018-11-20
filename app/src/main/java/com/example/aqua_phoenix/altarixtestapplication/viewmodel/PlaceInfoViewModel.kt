package com.example.aqua_phoenix.altarixtestapplication.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import com.example.aqua_phoenix.altarixtestapplication.repository.PlacesRepository

class PlaceInfoViewModel : ViewModel() {
    private lateinit var placeInfo: LiveData<PlaceInfo>

    fun init(placeInfoId: String, context: Context) {
        if (this::placeInfo.isInitialized) {
            return
        }
        placeInfo = PlacesRepository.getPlaceInfo(placeInfoId, context)
    }

    fun getPlaceInfo() = placeInfo
}