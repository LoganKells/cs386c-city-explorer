package com.example.cityexplorer.ui.main

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityexplorer.R
import com.example.cityexplorer.databinding.FragmentMainBinding
import com.example.cityexplorer.ui.location.LocationAdapter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {
    // The viewModel should be shared with the parent activity because when a user
    // returns to the home screen, we want to show the same Location that were previously loaded.
    // This is why we use activityViewModels instead of viewModels.
    private val viewModel: MainViewModel by activityViewModels()

    // The adapter is used to update values in the RecyclerView (ListAdapter).
    private lateinit var locationAdapter: LocationAdapter

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var saveFileLauncher: ActivityResultLauncher<Intent>

    /**
     * Set up the RecyclerView post data Adapter.
     * */
    private fun initAdapter(binding: FragmentMainBinding): LocationAdapter {
        val adapter = LocationAdapter(viewModel)
        binding.recyclerViewLocations.adapter = adapter

        // Add a simple layout manager so the recycler view knows how to scroll our entries
        binding.recyclerViewLocations.layoutManager = LinearLayoutManager(this.context)

        return adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)

        // This launcher is used to save the data to a file when the user clicks the export
        // to JSON button.
        saveFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                    it.data?.data?.also { uri ->
                        // Write data from the model to the new JSON file.
                        viewModel.exportToJson(uri)
                    }
                Log.d("MainFragment", "Created ActivityResultLauncher for saving JSON file.")
            }
        }

        return binding.root
    }

    private fun initObservers() {
        viewModel.observeLocations().observe(viewLifecycleOwner) {
            locationAdapter.submitList(it)
            locationAdapter.notifyDataSetChanged()
            if (it != null) {
                Log.d("MainFragment", "LocationAdapter updated with ${it.size} locations")
            } else {
                Log.d("MainFragment", "LocationAdapter updated with null locations")
            }
        }
    }

    private fun configureButtons() {
        // Parsing the list and removing all items where:
        // "Flag" is true, meaning they were checked
        // AND
        // "Start Flag" is false, meaning they are not a starting location
        // Finally, we display a Toast message in case we attempt to delete a starting location
        // and cancel the operation for that specific item (other selected items will be deleted).
        binding.buttonDeleteSelectedLocations.setOnClickListener {
            viewModel.removeSelectedLocations()
        }

        // This button will navigate to the LocationFragment for adding new locations.
        binding.fabAddLocation.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_LocationFragment)
        }

        //
        binding.fabOptimize.setOnClickListener {
            viewModel.calculateOrderOfLocations()
        }

        binding.buttonExportToJson.setOnClickListener {
            // Launch the intent to save the file.
            // See: https://developer.android.com/training/data-storage/shared/documents-files#open-file
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/json"
                putExtra(Intent.EXTRA_TITLE, "city_explorer_locations.json")
            }
            saveFileLauncher.launch(intent)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        // Adapter for displaying Locations in the RecyclerView.
        locationAdapter = initAdapter(binding)

        // Observe the LiveData from the viewModel.
        initObservers()

        // Configure the button listeners.
        configureButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}