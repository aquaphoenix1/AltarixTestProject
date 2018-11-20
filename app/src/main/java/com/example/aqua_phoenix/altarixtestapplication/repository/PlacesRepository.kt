package com.example.aqua_phoenix.altarixtestapplication.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.example.aqua_phoenix.altarixtestapplication.App
import com.example.aqua_phoenix.altarixtestapplication.MainActivity
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import com.google.android.gms.location.places.Places
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object PlacesRepository {
    private val placeInfoDAO by lazy {
        App.placeInfoDB.getPlaceInfoDB()
    }

    fun getPlaceInfo(placeInfoId: String, context: Context): LiveData<PlaceInfo> {
        refreshPlaceInfo(placeInfoId, context)
        return placeInfoDAO.load(placeInfoId)
    }

    private fun refreshPlaceInfo(placeInfoId: String, context: Context) {
        GlobalScope.launch {
            val hasPlaceInfo = placeInfoDAO.hasPlaceInfo(placeInfoId) > 0

            if (!hasPlaceInfo) {
                Places.getGeoDataClient(context).getPlaceById(placeInfoId)
                    .addOnCompleteListener { p0 ->
                        if (p0.isSuccessful) {
                            val places = p0.result
                            if (places != null) {
                                val place = places.get(0)

                                /*if (placeDetailsFragment != null) {
                                    deleteFragment(placeDetailsFragment!!, supportFragmentManager)
                                }

                                placeDetailsFragment = PlaceDetailsFragment()

                                val bundle = Bundle()
                                bundle.putCharSequence("address", place.address)

                                placeDetailsFragment!!.arguments = bundle

                                //deleteFragment()

                                replaceFragment(
                                    R.id.main_fragment_layout,
                                    placeDetailsFragment!!, supportFragmentManager
                                )*/

                                //var placeInfo = PlaceInfo(placeInfoId, place.address.toString())

                                places.release()

                                Places.getGeoDataClient(context).getPlacePhotos(placeInfoId)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // Get the list of photos.
                                            val photos = task.result
                                            // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                                            val photoMetadataBuffer = photos!!.photoMetadata
                                            // Get the first photo in the list.
                                            try {
                                                val photoMetadata = photoMetadataBuffer.get(0)

                                                // Get the attribution text.
                                                val attribution = photoMetadata.attributions
                                                // Get a full-size bitmap for the photo.
                                                val photoResponse =
                                                    Places.getGeoDataClient(context).getPhoto(photoMetadata)
                                                photoResponse.addOnCompleteListener { task ->
                                                    val photo = task.result
                                                    val bitmap = photo!!.bitmap

                                                    val file = File(
                                                        Environment.getExternalStoragePublicDirectory(
                                                            Environment.DIRECTORY_PICTURES
                                                        ), "$placeInfoId.jpg"
                                                    )

                                                    val stream: OutputStream = FileOutputStream(file)
                                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                                                    stream.flush()
                                                    stream.close()

                                                    //placeDetailsFragment!!.setImage(bitmap)
                                                    placeInfoDAO.save(
                                                        PlaceInfo(
                                                            placeInfoId,
                                                            place.address.toString(),
                                                            Uri.parse(file.absolutePath)
                                                        )
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                //placeDetailsFragment!!.setDefaultImage()
                                                placeInfoDAO.save(PlaceInfo(placeInfoId, place.address.toString()))
                                            }
                                        }
                                    }
                            }
                        }
                    }
            }
        }
    }

}