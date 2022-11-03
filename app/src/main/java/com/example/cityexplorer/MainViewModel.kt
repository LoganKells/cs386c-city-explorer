package com.example.cityexplorer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cityexplorer.api.Location
import com.example.cityexplorer.api.LocationApi
import com.example.cityexplorer.api.LocationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // The title value is observed to update the action bar title.
    private var title = MutableLiveData<String>()

    // The LocationReposity is the bridge between API and the application.
    private val locationApi = LocationApi.create()
    private val locationRepository = LocationRepository(locationApi)

    // Locations are observed in the MainFragment to display them in the RecyclerView.
    private val locations = MutableLiveData<List<Location>?>()

    /**
     * Locations will be refreshed from the local JSON file.
     * */
    fun refreshLocations() {
        viewModelScope.launch(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            val newLocations = locationRepository.getLocations()
            locations.postValue(newLocations)
        }
    }

    // ************** Action Bar ************************
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }


    // ************** Locations ************************
    fun updateLocations(locations: List<Location>) {
        this.locations.postValue(locations)
    }

    fun observeLocations(): MutableLiveData<List<Location>?> {
        return locations
    }

    fun addLocation(newLocation: Location) {
        val newLocations = locations.value?.toMutableList()
        newLocations?.add(newLocation)
        locations.postValue(newLocations)
    }

    fun removeLocation(locationIndex: Int) {
        //Log.d(javaClass.simpleName, "LOC TO DEL: $locationIndex")
        val newLocations = locations.value?.toMutableList()
        //Log.d(javaClass.simpleName, "BEFORE DELETE: $newLocations")
        if (newLocations?.get(locationIndex)?.flag == true) {
            newLocations.removeAt(locationIndex)
        }
        //Log.d(javaClass.simpleName, "AFTER DELETE: $newLocations")
        locations.postValue(newLocations)
    }
}