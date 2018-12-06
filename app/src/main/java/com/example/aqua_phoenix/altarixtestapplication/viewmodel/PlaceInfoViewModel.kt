package com.example.aqua_phoenix.altarixtestapplication.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Context
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import com.example.aqua_phoenix.altarixtestapplication.repository.PlacesRepository

class PlaceInfoViewModel : ViewModel() {
    private var placeInfo: MutableLiveData<PlaceInfo>? = null
    private lateinit var placeId: String

    /*private var fromId: String? = null
    private var toId: String? = null*/

    private var filteredJson: String? = null

    private var isLoaded: Boolean = false

    fun isLoaded(): Boolean {
        return isLoaded
    }

    fun setLoaded() {
        isLoaded = true
    }

    fun getFilteredJson(): String? {
        return filteredJson
    }

    fun setFilteredJson(json: String?) {
        filteredJson = json
    }

    fun init(placeInfoId: String, context: Context) {
        placeInfo?.let {
            if (placeId == placeInfoId) {
                return
            }
        }

        placeId = placeInfoId

        placeInfo = PlacesRepository.getPlaceInfo(placeInfoId, context)
    }

    fun resetPlace() {
        placeInfo = null
    }

    fun isInitializedPlaceInfo() = placeInfo != null

    fun getCurrentData(): PlaceInfo {
        return placeInfo?.value!!
    }

    fun getPlaceInfo() = placeInfo

    /*fun setFrom() {
        fromId = placeId
    }

    fun getFrom(): String? = fromId

    fun setTo() {
        toId = placeId
    }

    fun getTo(): String? = toId*/
}