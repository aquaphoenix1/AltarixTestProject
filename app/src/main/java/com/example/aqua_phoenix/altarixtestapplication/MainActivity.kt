package com.example.aqua_phoenix.altarixtestapplication

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.aqua_phoenix.altarixtestapplication.viewmodel.PlaceInfoViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


class MainActivity : AppCompatActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var comeWithSource = true

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        requestGPS()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        GPSPermissionEnabled()
    }

    private fun requestGPS() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
                .setRationale("Разрешение для поиска пользователя на карте")
                .setNegativeButtonText("Нет")
                .setPositiveButtonText("Да")
                .build()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*main_fragment_layout.postDelayed({
            val slide = Explode()

            slide.duration = 3000
            val fade = Fade()
            fade.duration = 5000
            val setTrans = TransitionSet()
            setTrans.ordering = TransitionSet.ORDERING_TOGETHER
            setTrans.addTransition(slide)
            setTrans.addTransition(fade)
            TransitionManager.beginDelayedTransition(mainLayout, setTrans)
            main_fragment_layout.visibility = View.GONE
//            main_fragment_layout.postDelayed({
//                TransitionManager.beginDelayedTransition(mainLayout, setTrans)
//                main_fragment_layout.visibility = View.VISIBLE
//
//            }, 1500)
        }, 5000)*/
        placeInfoViewModel = ViewModelProviders.of(this).get(PlaceInfoViewModel::class.java)
        val fr = supportFragmentManager.findFragmentByTag("mapFragment") as GoogleMapsFragment
        fr.getMapAsync(this)
    }



    private var locationCallback = object : LocationCallback() {
        fun animateCamera(location: Location) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(17f)                   // Sets the zoom
                .bearing(0f)                // Sets the orientation of the camera to east
                .tilt(0f)                   // Sets the tilt of the camera to 30 degrees
                .build()                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)

            if (p0 != null) {
                if (p0.lastLocation == null) {
                    return
                }

                if (comeWithSource) {
                    animateCamera(p0.lastLocation)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startCurrentLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    private var placeDetailsFragment: PlaceDetailsFragment? = null

    @SuppressLint("MissingPermission")
    private fun GPSPermissionEnabled() {
        mMap.isMyLocationEnabled = true

        mMap.setOnMyLocationButtonClickListener {
            comeWithSource = true
            true
        }

        mMap.setOnCameraMoveStartedListener {
            if (it != REASON_DEVELOPER_ANIMATION) {
                comeWithSource = false
            }
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        startCurrentLocationUpdates()
    }

    private lateinit var placeInfoViewModel: PlaceInfoViewModel

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true

        mMap.setOnPoiClickListener { p0 ->
            if (p0 != null) {
                val id = p0.placeId
                placeInfoViewModel.init(id, this)

                /*GlobalScope.launch {
                    Api.placeInformationApi.getPlaceInformation(id,"name,rating", "AIzaSyCmKmY5c5x5lvSMhmrCwHYSxhJvJz5G_Sk").enqueue(object : Callback<PlaceInfo>{
                        override fun onFailure(call: Call<PlaceInfo>, t: Throwable) {
                            Log.d("0000000000", call.toString())
                            var a = 0
                        }

                        override fun onResponse(call: Call<PlaceInfo>, response: Response<PlaceInfo>) {
                            Log.d("0000000000", response.toString())
                            var a = 0
                        }

                    })
                }*/

            }
        }

        mMap.setOnMapClickListener {
            if (placeDetailsFragment != null) {
                deleteFragment(placeDetailsFragment!!, supportFragmentManager)
            }
        }

        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            GPSPermissionEnabled()
        } else {
            requestGPS()
        }
    }
}
