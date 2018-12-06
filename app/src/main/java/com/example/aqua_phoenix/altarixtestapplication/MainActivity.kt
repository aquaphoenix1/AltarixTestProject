package com.example.aqua_phoenix.altarixtestapplication

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.Snackbar
import android.support.transition.Slide
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v7.app.AppCompatActivity
import android.view.Gravity.LEFT
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.aqua_phoenix.altarixtestapplication.entites.PlaceInfo
import com.example.aqua_phoenix.altarixtestapplication.fragments.GoogleMapsFragment
import com.example.aqua_phoenix.altarixtestapplication.viewmodel.PlaceInfoViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.place_details_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest

class MainActivity : AppCompatActivity(), OnMapReadyCallback, EasyPermissions.PermissionCallbacks {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var rootDetailsLayout: LinearLayout
    private var comeWithSource = true

    @BindView(R.id.checkBoxParks)
    lateinit var parksCheckbox: CheckBox

    @BindView(R.id.checkBoxBusiness)
    lateinit var businessCheckbox: CheckBox

    @BindView(R.id.filter_checkboxes_layout)
    lateinit var filterCheckboxesLayout: LinearLayout

    @BindView(R.id.filterButton)
    lateinit var filterButton: Button

    private lateinit var placeInfoViewModel: PlaceInfoViewModel

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    override fun onBackPressed() {
        if (rootDetailsLayout.visibility == View.VISIBLE) {
            rootDetailsLayout.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val fr = supportFragmentManager.findFragmentByTag("mapFragment")

        (fr as GoogleMapsFragment).getMapAsync(this)

        placeInfoViewModel = ViewModelProviders.of(this).get(PlaceInfoViewModel::class.java)

        if (!placeInfoViewModel.isLoaded()) {
            lottie_splash_screen.visibility = VISIBLE
            lottie_splash_screen.playAnimation()
        } else {
            main_fragment_layout.visibility = VISIBLE
        }

        rootDetailsLayout = findViewById(R.id.rootLayout)

        if (placeInfoViewModel.isInitializedPlaceInfo()) {
            showDetailedInfo()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (!placeInfoViewModel.isInitializedPlaceInfo()) {
            GlobalScope.launch {
                Thread.sleep(3000)
                runOnUiThread {
                    placeInfoViewModel.setLoaded()
                    main_fragment_layout.visibility = VISIBLE
                    lottie_splash_screen.cancelAnimation()
                    lottie_splash_screen.visibility = GONE
                }
            }
        }

        mMap = googleMap

        mMap.isBuildingsEnabled = true
        mMap.isIndoorEnabled = true

        mMap.setOnPoiClickListener { pointOfInterest ->
            pointOfInterest?.let {
                showDetailedInfo(it.placeId)
            }
        }

        mMap.setOnMapClickListener {
            if (rootDetailsLayout.visibility == View.VISIBLE) {
                hideDetailedInfo()
            }
        }

        methodRequiresPerm()

        filterMap(placeInfoViewModel.getFilteredJson())
    }

    private fun getVisibleJson(name: String, value: String): String {
        return "{\"featureType\": \"poi.$name\",\"stylers\":[{\"visibility\":\"$value\"}]}"
    }

    private fun getFilterJson(): String {
        var json = "["

        json += if (parksCheckbox.isChecked) {
            getVisibleJson("park", "on")
        } else {
            getVisibleJson("park", "off")
        }

        json += ','

        json += if (businessCheckbox.isChecked) {
            getVisibleJson("business", "on")
        } else {
            getVisibleJson("business", "off")
        }

        json += ']'

        return json
    }

    @OnClick(R.id.filterButton)
    fun setFilter() {
        if (filterCheckboxesLayout.visibility == GONE) {
            filterCheckboxesLayout.visibility = VISIBLE
        } else {
            filterCheckboxesLayout.visibility = GONE
            val json = getFilterJson()

            placeInfoViewModel.setFilteredJson(json)
            filterMap(placeInfoViewModel.getFilteredJson())
        }
    }

    private fun filterMap(json: String?) {
        try {
            json?.let {
                mMap.setMapStyle(
                    MapStyleOptions(json)
                )
            } ?: run {
                mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style
                    )
                )
            }

        } catch (e: Resources.NotFoundException) {
            Snackbar.make(rootLayout, "Ошибка фильтрации", 2000).show()
        }
    }

    private var locationCallback = object : LocationCallback() {
        fun animateCamera(location: Location) {
            val latLng = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder()
                .target(latLng)
                .zoom(17f)
                .bearing(0f)
                .tilt(0f)
                .build()
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)

            locationResult?.let {
                locationResult.lastLocation?.let {
                    if (comeWithSource) {
                        animateCamera(locationResult.lastLocation)
                    }
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

    private fun startLottieAnimation() {
        lottie_loader_view.playAnimation()
        imageIV.visibility = GONE
    }

    private fun stopLottieAnimation() {
        lottie_loader_view.cancelAnimation()
        lottie_loader_view.visibility = GONE
        imageIV.visibility = VISIBLE
    }

    private fun refreshUI(placeInfo: PlaceInfo?) {
        placeInfo?.let {
            addressTextView.text = it.address
            nameTextView.text = it.name
            typesTextView.text = it.types
            URLTextView.text = it.url
            raitingTextView.text = it.raiting
            phoneTextView.text = it.phone

            startLottieAnimation()

            it.imagePath?.let { path ->
                Picasso.get().load(path).into(imageIV, object : Callback {
                    override fun onSuccess() {
                        stopLottieAnimation()
                    }

                    override fun onError(e: Exception?) {
                        Snackbar.make(rootLayout, "Ошибка загрузки фото", 2000).show()
                        stopLottieAnimation()
                        imageIV.setImageResource(R.drawable.error)
                    }
                })
            } ?: run {
                stopLottieAnimation()
                imageIV.setImageResource(R.drawable.error)
            }
        }
    }

    private fun hideDetailedInfo() {
        rootDetailsLayout.visibility = View.INVISIBLE
        placeInfoViewModel.resetPlace()
        clearPlaceInfo()
    }

    private fun clearPlaceInfo() {
        addressTextView.text = applicationContext.getString(R.string.loadText)
        nameTextView.text = applicationContext.getString(R.string.loadText)
        typesTextView.text = applicationContext.getString(R.string.loadText)
        URLTextView.text = applicationContext.getString(R.string.loadText)
        raitingTextView.text = applicationContext.getString(R.string.loadText)
        phoneTextView.text = applicationContext.getString(R.string.loadText)
    }

    private fun showDetailedInfo(placeId: String = "") {
        val slide = Slide()
        slide.duration = 0
        slide.slideEdge = LEFT

        if (!placeId.isEmpty()) {
            clearPlaceInfo()
            initPlaceInfoViewModel(placeId)
            slide.duration = 1000
        } else {
            refreshUI(placeInfoViewModel.getCurrentData())
        }

        val setTrans = TransitionSet()
        setTrans.addTransition(slide)

        TransitionManager.beginDelayedTransition(rootDetailsLayout, setTrans)
        rootDetailsLayout.visibility = View.VISIBLE
    }

    private fun initPlaceInfoViewModel(placeId: String) {
        placeInfoViewModel.init(placeId, applicationContext)
        placeInfoViewModel.getPlaceInfo()?.observe(this, android.arch.lifecycle.Observer {
            refreshUI(it)
        })
    }

    private fun requestGPS() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this,
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .setRationale("Разрешение для поиска пользователя на карте")
                .setNegativeButtonText("Нет")
                .setPositiveButtonText("Да")
                .build()
        )
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    private fun methodRequiresPerm() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            GPSPermissionEnabled()
        } else {
            requestGPS()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        requestGPS()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        GPSPermissionEnabled()
    }

    /*private fun getEndLocationTitle(results: DirectionsResult): String {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" + results.routes[0].legs[0].distance.humanReadable
    }

    private fun addMarkersToMap(results: DirectionsResult) {
        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results.routes[0].legs[0].startLocation.lat,
                    results.routes[0].legs[0].startLocation.lng
                )
            ).title(results.routes[0].legs[0].startAddress)
        )

        mMap.addMarker(
            MarkerOptions().position(
                LatLng(
                    results.routes[0].legs[0].endLocation.lat,
                    results.routes[0].legs[0].endLocation.lng
                )
            ).title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results))
        )
    }

    private fun addPolyline(result: DirectionsResult) {
        val path = result.routes[0].overviewPolyline.decodePath()

        val line = PolylineOptions()
        val latLngBuilder = LatLngBounds.Builder()

        for (i in path) {
            line.add(com.google.android.gms.maps.model.LatLng(i.lat, i.lng))
            latLngBuilder.include(
                com.google.android.gms.maps.model.LatLng(
                    i.lat,
                    i.lng
                )
            )
        }

        line.width(16f).color(R.color.colorPrimary)

        mMap.addPolyline(line)
    }

    @BindView(R.id.buttonSource)
    lateinit var buttonSource: Button

    @OnClick(R.id.buttonSource)
    fun setFrom(){
        placeInfoViewModel.setFrom()
    }

    @BindView(R.id.buttonDestination)
    lateinit var buttonDestination: Button

    @OnClick(R.id.buttonDestination)
    fun setTo(){
        placeInfoViewModel.setTo()
    }

    @BindView(R.id.buildPath)
    lateinit var buildPath: Button

    @OnClick(R.id.buildPath)
    fun showDirection() {
        val from = "place_id:" + placeInfoViewModel.getFrom()
        val to = "place_id:" + placeInfoViewModel.getTo()

        from?.let {
            to?.let {
                try {
                    val geoApiContext = GeoApiContext()
                    geoApiContext.setApiKey(applicationContext.getString(R.string.strings_api_key))
                    val result = DirectionsApi.newRequest(geoApiContext)
                        .mode(TravelMode.WALKING)
                        .origin(from)
                        .destination(to)
                        .await()

                    addMarkersToMap(result)
                    addPolyline(result)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }*/
}