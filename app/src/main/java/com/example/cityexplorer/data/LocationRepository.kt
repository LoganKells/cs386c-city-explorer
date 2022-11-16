package com.example.cityexplorer.data

import com.example.cityexplorer.ui.main.MainActivity
import com.google.gson.GsonBuilder

class LocationRepository(private val locationApi: LocationApi) {
    val gson = GsonBuilder().registerTypeAdapter(
        String::class.java, LocationApi.Deserializer()
    ).create()

    // fun getLocations() = locationApi.getLocations()

    /**
     * This function simple gets List<Location> from the API, which loads data from a
     * local JSON file.
     * @return List<Location>
     * */
    fun getLocations(): List<Location> {
        return if (MainActivity.readFromJson) {
            // Read from JSON file and convert to Java Object.
            val response = gson.fromJson(
                MainActivity.locationFile,
                LocationApi.LocationResponse::class.java)
            unpackLocations(response)
        } else {
            emptyList()
        }
    }

    private fun unpackLocations(response: LocationApi.LocationResponse): List<Location> {
        val locationData = response.locations
        val allLocations: MutableList<Location> = mutableListOf()
        for (location in locationData) {
            allLocations.add(location)
        }
        return allLocations
    }


    private fun getLatitudeLongitude(location: Location) {
        TODO("This function will get the latitude and longitude of a location by using " +
                "the Geocode API that calls a HTTP GET request, passing an address in the URL.")
    }
}
