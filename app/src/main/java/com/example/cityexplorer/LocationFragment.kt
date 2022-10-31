package com.example.cityexplorer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        var newFavLocationList = ArrayList<FavLocation>()

        viewModel.observeLocations().observe(viewLifecycleOwner) {
            newFavLocationList = it
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

            val newFavLocation = FavLocation(formName, formRating, formAddress)

            newFavLocationList.add(newFavLocation)
        }

        binding.buttonMainMenu.setOnClickListener {

            viewModel.updateLocations(newFavLocationList)

            viewModel.observeLocations().observe(viewLifecycleOwner) {
                viewModel.updateLocations(newFavLocationList)
            }

            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}