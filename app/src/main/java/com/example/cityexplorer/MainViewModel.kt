package com.example.cityexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var getLocations = MutableLiveData<ArrayList<Location>>()

    fun updateLocations(locations: ArrayList<Location>) {
        getLocations.postValue(locations)
    }

    fun observeLocations(): LiveData<ArrayList<Location>> {
        return getLocations
    }
}