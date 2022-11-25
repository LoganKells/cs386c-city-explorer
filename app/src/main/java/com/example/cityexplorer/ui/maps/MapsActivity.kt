package com.example.cityexplorer.ui.maps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.cityexplorer.R
import com.example.cityexplorer.databinding.ActivityMapsBinding
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var activityMapsBinding: ActivityMapsBinding

    // True only if the user has granted the location permission.
    private var locationPermissionGranted = false

    // The LatLng is used to move the map to the selected location.
    private var markerLatitudeLongitude: LatLng = LatLng(-33.852, 151.211)
    private var markerTitle = "Sydney"
    private val defaultZoomLevel = 15f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the content view that renders the map.
        activityMapsBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(activityMapsBinding.root)
        setSupportActionBar(activityMapsBinding.toolbar)

        // Verify the Google Play Services APK is available and up to date.
        // Also request required permissions.
        checkGooglePlayServices()
        requestPermission()

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment: SupportMapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if( locationPermissionGranted ) {
            // NOTE -we checked location permissions in requestPermission, but the compiler
            //  might complain about our not checking it.
            //  So the below check is making compiler happy.
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Consider calling ActivityCompat#requestPermissions here to request the
                // missing permissions, and then overriding public void
                // onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }

            // If we have location permissions, then enable the “my location” function on the map
            // (i.e., the blue dot) and enable the “find my location” button on the map.
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }

        // Data passed to this activity about the Location that was selected by the user.
        getDataFromIntent()

        // Update the action bar title
        supportActionBar?.title = "Location: $markerTitle"

        // Add a marker and zoom to the selected location
        googleMap.addMarker(
            MarkerOptions()
                .position(markerLatitudeLongitude)
                .title(markerTitle)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatitudeLongitude, defaultZoomLevel))
    }

    private fun getDataFromIntent() {
        val activityThatCalled = intent
        val callingBundle = activityThatCalled.extras
        if (callingBundle != null) {
            val locationLatitude: Double = callingBundle.getDouble("EXTRA_LOCATION_LATITUDE")
            val locationLongitude: Double = callingBundle.getDouble("EXTRA_LOCATION_LONGITUDE")
            val locationNickName: String? = callingBundle.getString("EXTRA_LOCATION_NICKNAME")

            if (locationLatitude != 0.0 && locationLongitude != 0.0 && locationNickName != null) {

                // Update the marker with the location data passed to this activity.
                // Also, these values are used to zoom the map to the selected location.
                markerLatitudeLongitude = LatLng(locationLatitude, locationLongitude)
                markerTitle = locationNickName

            }
        }
    }

    /**
     * From flipped classroom 9
     * */
    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode =
            googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 257)?.show()
            } else {
                Log.i(javaClass.simpleName,
                    "This device must install Google Play Services.")
                finish()
            }
        }
    }

    /**
     * From flipped classroom 9
     * */
    private fun requestPermission() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    locationPermissionGranted = true
                } else -> {
                Toast.makeText(this,
                    "Unable to show location - permission required",
                    Toast.LENGTH_LONG).show()
            }
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
    }


}