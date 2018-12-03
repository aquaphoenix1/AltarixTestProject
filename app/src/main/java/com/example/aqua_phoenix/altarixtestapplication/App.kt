package com.example.aqua_phoenix.altarixtestapplication

import android.app.Application
import android.content.Context
import com.example.aqua_phoenix.altarixtestapplication.data_base.PlaceInfoDB
import com.example.aqua_phoenix.altarixtestapplication.http.PlaceInformationRetrofit

class App : Application() {
    companion object {
        lateinit var placeInfoDB: PlaceInfoDB
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        PlaceInformationRetrofit.init()
        appContext = applicationContext
        placeInfoDB = PlaceInfoDB.getInstance(this)
    }
}