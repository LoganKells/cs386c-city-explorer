package com.example.cityexplorer.ui.main

import android.app.Application
import android.location.Geocoder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cityexplorer.data.Location
import com.example.cityexplorer.data.LocationJsonApi
import com.example.cityexplorer.data.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    // The title value is observed to update the action bar title.
    private var title = MutableLiveData<String>()

    // The LocationReposity is the bridge between API and the application.
    private val locationJsonApi = LocationJsonApi.create()
    private val locationRepository = LocationRepository(locationJsonApi)

    // Locations are observed in the MainFragment to display them in the RecyclerView.
    private val locations = MutableLiveData<List<Location>?>()


    // ************** Action Bar ************************
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }


    // ************** Locations ************************
    fun observeLocations(): MutableLiveData<List<Location>?> {
        return locations
    }

    private fun getLocations(): List<Location>? {
        return locations.value
    }

    // ************** IO ************************

    /**
     * Locations in the view model will be refreshed from the local JSON file.
     * */
    fun refreshLocationsFromJson() {
        val fileName = MainActivity.locationFileName
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val jsonData: String = getApplication<Application>().openFileInput(fileName)
                .bufferedReader().use { it.readText() }
            val response = locationRepository.gson.fromJson(jsonData, LocationJsonApi.LocationResponse::class.java)
            val newLocations = locationRepository.unpackLocations(response)

            // Update the locations in the view model.
            locations.postValue(newLocations)
        }
    }

    /**
     * Add a location to the view model then save it to the local JSON file.
     * */
    fun addLocation(location: Location, geocoder: Geocoder) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val newLocation = locationRepository.calculateLocation(location, geocoder)
            val newLocations = locations.value?.toMutableList()
            newLocations?.add(newLocation)

            // Update model
            locations.postValue(newLocations)

            // Sync JSON file to model
            saveLocationsToJson(newLocations!!)
        }
    }

    /**
     * Save the Locations in the view model to the JSON file.
     * */
    private fun saveLocationsToJson(locations: List<Location>) {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val app = getApplication<Application>()
            val locationResponse = LocationJsonApi.LocationResponse(locations)
            val jsonData: String = LocationJsonApi.convertToJson(locationResponse)
            app.applicationContext.openFileOutput(MainActivity.locationFileName, AppCompatActivity.MODE_PRIVATE)
                .use {
                    it.write(jsonData.toByteArray())
                    Log.d("MainViewModel", "Saved locations to JSON file. ${locations.size} locations.")
                }

            // Sync the locations in the view model with the JSON file.
            refreshLocationsFromJson()
        }
    }

    /**
     * Function to remove locations selected by the user in the RecyclerView.
     * */
    fun removeSelectedLocations() {
        val locationsData: MutableList<Location>? = getLocations() as MutableList<Location>?
        if (locationsData != null) {
            var deletedLocations = 0

            // Remove all selected Locations from the list if they are not a starting location.
            locationsData.removeAll { location ->
                if (location.deleteFlag && !location.startFlag) {
                    deletedLocations++
                    true
                } else {
                    false
                }
            }
        }
        // update model
        locations.postValue(locationsData)

        // Sync JSON file to model
        saveLocationsToJson(locationsData!!)
    }

    /**
     * Override a Location in the view model with a new Location.
     * This is useful whenever the user modifies a Location in the UI.
     * */
    fun updateLocation(location: Location, idx: Int) {
        val locationsData: MutableList<Location>? = getLocations() as MutableList<Location>?
        if (locationsData != null) {
            locationsData[idx] = location
        }

        // Update model
        locations.postValue(locationsData)

        // REVIEW - trying to minimize the number of times we save to JSON here.
        //  If this is not called, then the JSON file is out of sync with the view model.
        //  until the user adds or deletes a Location.
        // saveLocationsToJson(locationsData!!)
    }

    /**
     * The "Algorithm" for optimizing the sorting of the locations
     * */
    fun calculateOrderOfLocations() {
        val locationsData = getLocations()
        // Updated list variable
        // Careful: We have a "Location" data class... but also...
        // a "Location" android import, which is renamed to KotlinLocation
        // and is used in order to calculate distances between locations.
        // However, Kotlin may still regard "Location" as the import!
        // Thus, we need to be careful whenever we make use of "Location".

        // The final updated list. We will build it.
        val updatedList = mutableListOf<Location>()

        // Initialize variables
        val curLocation = android.location.Location("")
        val nextLocation = android.location.Location("")
        var curToNextDistance: Float

        // Find starting location and get coordinates
        if (locationsData != null) {
            for (location in locationsData) {
                if (location.startFlag) {
                    curLocation.latitude = location.latitude
                    curLocation.longitude = location.longitude
                }
            }
        }

        // While the updated list is not yet completely filled
        while (updatedList.size < locationsData?.size!!) {

            // initialize variables
            var minCurToNextDistance = 100000000000000F
            var targetIndex = -1

            // parse the entire list of locations
            for ((index, location) in locationsData.withIndex()) {
                // check to skip the current location, as it cannot be the next one
                if (location.latitude != curLocation.latitude && location.longitude != curLocation.longitude) {

                    // get coordinates of the candidate next location
                    nextLocation.latitude = location.latitude
                    nextLocation.longitude = location.longitude

                    // calculate distance between current location and candidate next location
                    curToNextDistance = curLocation.distanceTo(nextLocation)

                    // keep the location index that achieves the minimum distance
                    if (curToNextDistance < minCurToNextDistance) {
                        minCurToNextDistance = curToNextDistance
                        targetIndex = index
                    }
                }

                // get the next location
                val targetLocation = locationsData[targetIndex]

                // add it to the updated list
                updatedList.add(locationsData[targetIndex])

                // set the current location to the location we chose to go to
                curLocation.latitude = targetLocation.latitude
                curLocation.longitude = targetLocation.longitude
            }
        }

        // submit list when done
        locations.postValue(updatedList)
    }




}