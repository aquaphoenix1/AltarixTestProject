package com.example.aqua_phoenix.altarixtestapplication.entites

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import android.net.Uri
import com.example.aqua_phoenix.altarixtestapplication.R


@Entity
data class PlaceInfo(
    @PrimaryKey var id: String = "",
    var address: String = "",
    var name: String = "",
    var types: String = "",
    var url: String = "",
    var raiting: String = "",
    var phone: String = "",
    var imagePath: String? = null,
    var geometry: String? = null
)


fun getDefaultPlace(context: Context): PlaceInfo {
    return PlaceInfo(
        "",
        context.getString(R.string.defaultAddress),
        context.getString(R.string.defaultName),
        context.getString(R.string.defaultTypes),
        context.getString(R.string.defaultURL),
        context.getString(R.string.defaultRating),
        context.getString(R.string.defaultPhone),
        null,
        null
    )
}