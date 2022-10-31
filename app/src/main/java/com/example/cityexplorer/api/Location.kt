package com.example.cityexplorer.api

data class Location(
    val nickname: String,
    val country: String,
    val state: String,
    val city: String,
    val address1: String,
    val address2: String,
    val postCode: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Int,
    val duration: Int,
    val comments: String,
    val rank: Int,
    ) {

    override fun equals(other: Any?): Boolean {
        // TODO - what if Latitude and Longitude are not populated?
        // FIXME - Why can we add multiple locations with the same values when we load ViewModel
        //  from JSON file?
        return if (other is Location) {
            latitude == other.latitude && longitude == other.longitude
        } else {
            false
        }
    }
}