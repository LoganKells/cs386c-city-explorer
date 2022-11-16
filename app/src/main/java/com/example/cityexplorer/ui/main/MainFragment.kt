package com.example.cityexplorer.ui.main

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
import com.example.cityexplorer.R
import com.example.cityexplorer.databinding.FragmentMainBinding
import com.example.cityexplorer.ui.location.LocationAdapter
import android.location.Location as KotlinLocation

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
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        locationAdapter = initAdapter(binding)

        viewModel.observeLocations().observe(viewLifecycleOwner) {
            // The submitList method will update the RecyclerView.
            locationAdapter.submitList(it)
        }

        // Parsing the list and removing all items where:
        // "Flag" is true, meaning they were checked
        // AND
        // "Start Flag" is false, meaning they are not a starting location
        // Finally, we display a Toast message in case we attempt to delete a starting location
        // and cancel the operation for that specific item (other selected items will be deleted).
        binding.buttonDeleteSelectedLocations.setOnClickListener {
            viewModel.observeLocations().observe(viewLifecycleOwner) {
                it?.forEachIndexed { index, _ ->
                    if (it[index].flag) {
                        if (it[index].startFlag) {
                            Toast.makeText(context, "Cannot remove starting location!", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            viewModel.removeLocation(index)
                        }
                    }
                }
            }
            findNavController().navigate(R.id.action_MainFragment_to_LocationFragment)
            findNavController().navigate(R.id.action_LocationFragment_to_MainFragment)
        }

        binding.fabAddLocation.setOnClickListener {
            viewModel.observeLocations().observe(viewLifecycleOwner) {
                it?.forEachIndexed { _, location ->
                    location.flag = false
                }
                locationAdapter.submitList(it)
            }
            findNavController().navigate(R.id.action_MainFragment_to_LocationFragment)
        }

        // The "Algorithm"
        binding.fab.setOnClickListener {
            viewModel.observeLocations().observe(viewLifecycleOwner) {
                // Updated list variable
                // Careful: We have a "Location" data class... but also...
                // a "Location" android import, which is renamed to KotlinLocation
                // and is used in order to calculate distances between locations.
                // However, Kotlin may still regard "Location" as the import!
                // Thus, we need to be careful whenever we make use of "Location".

                // The final updated list. We will build it.
                val updatedList = mutableListOf<com.example.cityexplorer.data.Location>()

                // Initialize variables
                val curLocation = KotlinLocation("")
                val nextLocation = KotlinLocation("")
                var curToNextDistance: Float

                // Find starting location and get coordinates
                it?.forEachIndexed { index, _ ->
                    if (it[index].startFlag) {
                        curLocation.latitude = it[index].latitude
                        curLocation.longitude = it[index].longitude
                    }
                }

                // While the updated list is not yet completely filled
                while (updatedList.size < it?.size!!) {

                    // initialize variables
                    var minCurToNextDistance = 100000000000000F
                    var targetIndex = -1

                    // parse the entire list of locations
                    it.forEachIndexed { nextIndex, _ ->
                        // check to skip the current location, as it cannot be the next one
                        if (it[nextIndex].latitude != curLocation.latitude && it[nextIndex].longitude != curLocation.longitude) {

                            // get coordinates of the candidate next location
                            nextLocation.latitude = it[nextIndex].latitude
                            nextLocation.longitude = it[nextIndex].longitude

                            // calculate distance between current location and candidate next location
                            curToNextDistance = curLocation.distanceTo(nextLocation)

                            // keep the location index that achieves the minimum distance
                            if (curToNextDistance < minCurToNextDistance) {
                                minCurToNextDistance = curToNextDistance
                                targetIndex = nextIndex
                            }
                        }

                        // get the next location
                        val targetLocation = it[targetIndex]

                        // add it to the updated list
                        updatedList.add(it[targetIndex])

                        // set the current location to the location we chose to go to
                        curLocation.latitude = targetLocation.latitude
                        curLocation.longitude = targetLocation.longitude
                    }
                }
                // submit list when done
                locationAdapter.submitList(updatedList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}