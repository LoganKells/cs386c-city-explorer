package com.example.cityexplorer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var getLocations = MutableLiveData<ArrayList<FavLocation>>()

    fun updateLocations(data: ArrayList<FavLocation>) {
        getLocations.postValue(data)
    }

    fun observeLocations(): LiveData<ArrayList<FavLocation>> {
        return getLocations
    }
}