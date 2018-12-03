package com.example.aqua_phoenix.altarixtestapplication.entites

data class PlaceInformation(
    val html_attributions: List<Any>,
    val result: Result,
    val status: String
)

data class Result(
    val address_components: List<AddressComponent>,
    val formatted_phone_number: String,
    val geometry: Geometry,
    val name: String,
    val photos: List<Photo>,
    val rating: Double,
    val types: List<String>,
    val url: String
)

data class Geometry(
    val location: Location,
    val viewport: Viewport
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Southwest(
    val lat: Double,
    val lng: Double
)

data class Northeast(
    val lat: Double,
    val lng: Double
)

data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)