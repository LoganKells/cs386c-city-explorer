package com.example.cityexplorer.data

data class Location(
    val nickname: String,
    var country: String,
    var state: String,
    var city: String,
    var address1: String,
    var address2: String,
    var postCode: String,
    var latitude: Double,
    var longitude: Double,
    var rating: Float,
    val duration: Float,
    val comments: String,
    val rank: Int,
    var deleteFlag: Boolean,
    var startFlag: Boolean
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