package com.example.aqua_phoenix.altarixtestapplication.data_access

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo

@Dao
interface PlaceInfoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(placeInfo: PlaceInfo)

    @Query("SELECT * FROM placeinfo WHERE id = :placeId")
    fun load(placeId: String): PlaceInfo

    @Query("SELECT COUNT(id) FROM placeinfo WHERE id = :placeId")
    fun hasPlaceInfo(placeId: String): Int
}