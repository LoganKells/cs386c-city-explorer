package com.example.cityexplorer.ui.location

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cityexplorer.ui.main.MainViewModel
import com.example.cityexplorer.R
import com.example.cityexplorer.data.Location
import com.example.cityexplorer.databinding.FragmentLocationBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LocationFragment : Fragment() {
    // The viewModel should be shared with the parent activity because when a user
    // returns to the home screen, we want to show the same Location that were previously loaded.
    // This is why we use activityViewModels instead of viewModels.
    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentLocationBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        geocoder = Geocoder(context, Locale.getDefault())

        var tmp = false

        binding.switchCompatUserLocation.setOnClickListener {
            tmp = tmp != true
        }

        // TODO - there are no input validations, leading to crashes...
        binding.buttonSaveLocation.setOnClickListener {
            val formNickName = binding.editTextNickname.text.toString()
            val country = "United States"
            val formCity = binding.editTextCity.text.toString()
            // TODO - get the state from the spinner
            val formState = "CA"
            val formAddress1 = binding.editTextAddressLine1.text.toString()
            val formAddress2 = binding.editTextAddressLine2.text.toString()
            val formPostCode = binding.editTextZipCode.text.toString()
            val formRating = binding.editTextRating.text.toString()
            val formDuration = binding.editTextDurationAtLocation.text.toString()
            // TODO - we said we would have comments in the proposal.
            val formComments = ""
            // TODO - we need to calculate rank.
            val rank = -1
            // Flag to help for the deletion step
            val flag = false
            // Flag to identify starting location
            val startFlag = tmp

            val address = "$formAddress1 $formAddress2, $formCity, $formPostCode"

            // Discard non-valid address
            if (address.length <= 5) {
                Toast.makeText(context, "Please provide an address!", Toast.LENGTH_SHORT).show()
            }
            // Must insert rating or ".toInt()" crashes the app
            else if (formRating.isEmpty()) {
                Toast.makeText(context, "Please provide a valid rating!", Toast.LENGTH_SHORT).show()
            }
            // Must insert duration or ".toInt()" crashes the app
            else if (formDuration.isEmpty()) {
                Toast.makeText(context, "Please provide a valid duration!", Toast.LENGTH_SHORT).show()
            }
            else {
                val newAddress = geocoder.getFromLocationName(address, 1)
                if(newAddress.isEmpty()){
                    Toast.makeText(context, "The address does not exist!", Toast.LENGTH_SHORT).show()
                }
                else {
                    // Instead of user input, we use standardized input from geolocation API.
                    // Perhaps we should directly use "completeAddress", but then we need to change the adapter.
                    // And we will also have to change the Location data class accordingly.
                    val newLocation = newAddress[0]
                    val completeAddress = newLocation.getAddressLine(0)
                    val latitude = newLocation.latitude
                    val longitude = newLocation.longitude
                    val locAddress = newLocation.featureName + " " + newLocation.thoroughfare
                    val locPostCode = newLocation.postalCode
                    val locCity = newLocation.locality
                    val locState = newLocation.adminArea
                    val locCountry = newLocation.countryCode

                    // Checking for duplicates based on lat/long coordinates.
                    var foundDuplicate = false
                    viewModel.observeLocations().observe(viewLifecycleOwner) {
                        it?.forEachIndexed { index, _ ->
                            if (it[index].latitude == latitude && it[index].longitude == longitude) {
                                foundDuplicate = true
                            }
                        }
                    }

                    // We only add the location if it doesn't already exist in the list
                    if (!foundDuplicate) {

                        // We could potentially move this piece of code on Main Fragment
                        // It only keeps the last (as per list index) declared starting location.
                        var foundStart = false
                        if (tmp) {
                            viewModel.observeLocations().observe(viewLifecycleOwner) {
                                it?.asReversed()?.forEachIndexed { index, _ ->
                                    if (foundStart) {
                                        it[index].startFlag = false
                                    }

                                    if (it[index].startFlag) {
                                        foundStart = true
                                    }
                                }
                            }
                        }

                        val newLocationToAdd = Location(
                            formNickName, locCountry, locState, locCity, locAddress, "",
                            locPostCode, latitude, longitude, formRating.toInt(), formDuration.toInt(), formComments, rank, flag, startFlag)

                        // NOTE - all new Location should be added to the END of the list. The Location in the
                        //  view model will be sorted based on rank when the user clicks the "Explore" button.
                        viewModel.addLocation(newLocationToAdd)
                    }
                }
            }
        }

        binding.buttonMainMenu.setOnClickListener {
            findNavController().navigate(R.id.action_LocationFragment_to_MainFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}