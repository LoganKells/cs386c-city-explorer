package com.example.cityexplorer.data

import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.gson.GsonBuilder

class LocationRepository(private val locationJsonApi: LocationJsonApi) {
    val gson = GsonBuilder().registerTypeAdapter(
        String::class.java, LocationJsonApi.Deserializer()
    ).create()

    fun unpackLocations(response: LocationJsonApi.LocationResponse): List<Location> {
        val locationData = response.locations
        val allLocations: MutableList<Location> = mutableListOf()
        for (location in locationData) {
            allLocations.add(location)
        }
        return allLocations
    }

    /**
     * Calculate a true Address using Geocoder. Then update the Location data.
     * */
    fun calculateAddressUpdateLocation(location: Location, geocoder: Geocoder): Location {

        // Encode the location to a android.location.Address
        val addressesFromGeocoder: MutableList<Address> = geocoder.getFromLocationName(location.address1, 1)
        val addressRetrieved: Address ?= addressesFromGeocoder[0]
        if (addressRetrieved != null) {
            // val completeAddress = addressRetrieved.getAddressLine(0)
            location.latitude = addressRetrieved.latitude
            location.longitude = addressRetrieved.longitude
            location.address1 = (addressRetrieved.subThoroughfare ?: "") +
                    " " + (addressRetrieved.thoroughfare ?: "")

            // Noticed that these field are equivalent if there is no address2 entered by user
            // and not equal if there is an address2 entered by user such as "#302".
            if (addressRetrieved.subThoroughfare != addressRetrieved.featureName) {
                location.address2 = addressRetrieved.featureName ?: ""
            }

            if (addressRetrieved.postalCode == null) {
                location.postCode = ""
            }
            else {
                location.postCode = addressRetrieved.postalCode
            }

            location.city = addressRetrieved.locality
            location.state = addressRetrieved.adminArea
            location.country = addressRetrieved.countryCode
        } else {
            Log.d(javaClass.simpleName, "addLocation(): addressRetrieved is null")
        }
        return location
    }

}
