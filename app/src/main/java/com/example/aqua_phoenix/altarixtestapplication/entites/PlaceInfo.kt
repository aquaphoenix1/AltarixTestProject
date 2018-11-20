package com.example.aqua_phoenix.altarixtestapplication.entites

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.net.Uri

@Entity
data class PlaceInfo(
    @PrimaryKey var id: String = "",
    var address: String = "",
    @Ignore
    var imagePath: Uri? = null
)