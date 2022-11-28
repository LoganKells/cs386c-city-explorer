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

    private fun validateUserInput(
        addressFormatted: String,
        formRating: String,
        formDuration: String): Boolean {
        // Location data must be valid before saving.
        var validUserDataForLocation = true

        // Discard non-valid address
        if (addressFormatted.length <= 5) {
            Toast.makeText(context, "Please provide an address!", Toast.LENGTH_SHORT).show()
            Log.d("LocationFragment validateUserInput()", "Address is too short: $addressFormatted")
            validUserDataForLocation = false
        } else {
            // Try to get the geocoded address using Google.
            val addressFromGeocoder = geocoder.getFromLocationName(addressFormatted, 1)
            if (addressFromGeocoder == null || addressFromGeocoder.isEmpty()) {
                Log.d("LocationFragment validateUserInput()",
                    "addressFormatted is not valid for geocoder: $addressFormatted")
                Toast.makeText(context, "The address does not exist!", Toast.LENGTH_SHORT).show()
                validUserDataForLocation = false
            } else {
                val currentLocations: List<Location>? = viewModel.getLocations()

                // Check if the address is already in the list of locations.
                if (currentLocations != null) {
                    for ((idx, location) in currentLocations.withIndex()) {
                        if (currentLocations[idx].latitude == addressFromGeocoder[0].latitude
                            && currentLocations[idx].longitude == addressFromGeocoder[0].longitude) {
                            Toast.makeText(context, "The address is already in the list!",
                                Toast.LENGTH_SHORT).show()
                            Log.d("LocationFragment validateUserInput()",
                                "Address is already in the list: $addressFormatted")
                            validUserDataForLocation = false
                            break
                        }
                    }
                }
            }
        }
        // Must insert rating or ".toInt()" crashes the app
        if (formRating.isEmpty()) {
            Log.d("LocationFragment validateUserInput()", "formRating is empty")
            Toast.makeText(context, "Please provide a valid rating!", Toast.LENGTH_SHORT).show()
            validUserDataForLocation = false
        }
        // Must insert duration or ".toInt()" crashes the app
        if (formDuration.isEmpty()) {
            Log.d("LocationFragment validateUserInput()", "formDuration is empty")
            Toast.makeText(context, "Please provide a valid duration!", Toast.LENGTH_SHORT).show()
            validUserDataForLocation = false
        }

        return validUserDataForLocation
    }

    /**
     * Clear all the user's inputs.
     * */
    private fun clearForm() {
        binding.editTextNickname.text.clear()
        binding.editTextAddressLine1.text.clear()
        binding.editTextAddressLine2.text.clear()
        binding.editTextCity.text.clear()
        binding.editTextZipCode.text.clear()
        binding.editTextRating.text.clear()
        binding.editTextDurationAtLocation.text.clear()
        binding.editTextComments.text.clear()
        binding.switchCompatUserLocation.isChecked = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        geocoder = Geocoder(context, Locale.getDefault())

        var startingLoc = false

        binding.switchCompatUserLocation.setOnClickListener {
            startingLoc = startingLoc != true
        }

        binding.buttonSaveLocation.setOnClickListener {
            val formNickName = binding.editTextNickname.text.toString()
            val formCity = binding.editTextCity.text.toString()
            val formAddress1 = binding.editTextAddressLine1.text.toString()
            val formAddress2 = binding.editTextAddressLine2.text.toString()
            val formPostCode = binding.editTextZipCode.text.toString()
            val formRating = binding.editTextRating.text.toString()
            val formDuration = binding.editTextDurationAtLocation.text.toString()
            val formComments = binding.editTextComments.text.toString()

            // Validate user input as an address with rating and duration.
            val addressFormatted = "$formAddress1 $formAddress2, $formCity, $formPostCode"
            val validUserDataForLocation: Boolean =
                validateUserInput(addressFormatted, formRating, formDuration)

            // Only save the location if all the data is validated.
            if (validUserDataForLocation) {
                val newLocationToAdd = Location(
                    nickname = formNickName,
                    country = "",
                    state = "",
                    city = "",
                    address1 = addressFormatted,
                    address2 = "",
                    postCode = "",
                    latitude = 0.0,
                    longitude = 0.0,
                    rating = formRating.toInt(),
                    duration = formDuration.toInt(),
                    comments = formComments,
                    rank = -1,
                    deleteFlag = false,
                    startFlag = startingLoc
                )

                viewModel.addLocation(newLocationToAdd, geocoder, startingLoc)
                // Clear the edit text fields.
                clearForm()
                startingLoc = false
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