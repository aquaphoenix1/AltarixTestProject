package com.example.aqua_phoenix.altarixtestapplication.data_base

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.aqua_phoenix.altarixtestapplication.data_access.PlaceInfoDAO
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo

@Database(
    entities = [PlaceInfo::class],
    exportSchema = false,
    version = 1
)
abstract class PlaceInfoDB : RoomDatabase() {
    abstract fun getPlaceInfoDB(): PlaceInfoDAO

    companion object {
        private var instance: PlaceInfoDB? = null

        const val PLACE_INFO_DATABASE_NAME = "places_info.db"

        fun getInstance(context: Context): PlaceInfoDB {
            if (instance == null) {
                synchronized(PlaceInfoDB::class) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context,
                            PlaceInfoDB::class.java,
                            PLACE_INFO_DATABASE_NAME
                        ).build()
                    }
                }
            }
            return instance!!
        }
    }
}