package com.example.cityexplorer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.cityexplorer.databinding.FragmentLocationBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var newLocationList = ArrayList<Location>()

        viewModel.observeLocations().observe(viewLifecycleOwner) {
            newLocationList = it
        }

        binding.buttonSaveLocation.setOnClickListener {
            val formName = binding.editTextNickname.text.toString()
            val formAddress1 = binding.editTextAddressLine1.text.toString()
            val formAddress2 = binding.editTextAddressLine2.text.toString()
            val formCity = binding.editTextCity.text.toString()
            val formZipCode = binding.editTextZipCode.text.toString()
            val formRating = binding.editTextRating.text.toString()
            val formLengthOfStay = binding.editTextDurationAtLocation.text.toString()

            val formAddress = "$formAddress1, $formAddress2, $formCity, $formZipCode"

            val newLocation = Location(formName, formRating, formAddress)

            newLocationList.add(newLocation)
        }

        binding.buttonMainMenu.setOnClickListener {

            viewModel.updateLocations(newLocationList)

            viewModel.observeLocations().observe(viewLifecycleOwner) {
                viewModel.updateLocations(newLocationList)
            }

            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}