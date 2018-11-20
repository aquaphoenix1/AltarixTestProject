package com.example.aqua_phoenix.altarixtestapplication

import android.app.Application
import com.example.aqua_phoenix.altarixtestapplication.data_access.PlaceInfoDAO
import com.example.aqua_phoenix.altarixtestapplication.data_base.PlaceInfoDB
import com.example.aqua_phoenix.altarixtestapplication.http.Api

class App : Application() {
    companion object {
        lateinit var placeInfoDB: PlaceInfoDB
    }

    override fun onCreate() {
        super.onCreate()

        Api.init()

        placeInfoDB = PlaceInfoDB.getInstance(this)
    }
}