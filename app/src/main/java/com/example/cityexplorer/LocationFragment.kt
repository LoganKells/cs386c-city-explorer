package com.example.cityexplorer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cityexplorer.api.Location
import com.example.cityexplorer.databinding.FragmentLocationBinding

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
            val latitude = 0.0
            val longitude = 0.0
            val formRating = binding.editTextRating.text.toString().toInt()
            val formDuration = binding.editTextDurationAtLocation.text.toString().toInt()
            // TODO - we said we would have comments in the proposal.
            val formComments = ""
            // TODO - we need to calculate rank.
            val rank = -1

            val newLocation = Location(
                formNickName, country, formState, formCity, formAddress1, formAddress2,
                formPostCode, latitude, longitude, formRating, formDuration, formComments, rank)

            // NOTE - all new Location should be added to the END of the list. The Location in the
            //  view model will be sorted based on rank when the user clicks the "Explore" button.
            viewModel.addLocation(newLocation)
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