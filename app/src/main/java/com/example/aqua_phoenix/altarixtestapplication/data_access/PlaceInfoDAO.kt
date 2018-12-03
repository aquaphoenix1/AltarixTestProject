package com.example.aqua_phoenix.altarixtestapplication.data_access

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo

@Dao
interface PlaceInfoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(placeInfo: PlaceInfo)

    @Query("SELECT * FROM placeinfo WHERE id = :placeId")
    fun load(placeId: String): PlaceInfo

    @Query("SELECT COUNT(id) FROM placeinfo WHERE id = :placeId")
    fun hasPlaceInfo(placeId: String): Int

   /* @Query("IF EXISTS(SELECT id FROM placeinfo WHERE id = :placeId) UPDATE placeinfo SET imagePath = :uri WHERE id = :placeId ELSE INSERT placeinfo(id, imagePath) values(:placeId, :uri)")
    fun saveImage(placeId: String, uri: String)*/
}