package com.example.cityexplorer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cityexplorer.databinding.FragmentMainBinding

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

        binding.buttonDeleteSelectedLocations.setOnClickListener {
            viewModel.observeLocations().observe(viewLifecycleOwner) {
                it?.forEachIndexed { index, location ->
                    Log.d(javaClass.simpleName, "THATTTT$location")
                    if (it[index].flag) {
                        viewModel.removeLocation(index)
                    }
                }
                locationAdapter.submitList(it)
            }
        }

        binding.fabAddLocation.setOnClickListener {
            viewModel.observeLocations().observe(viewLifecycleOwner) {
                // The submitList method will update the RecyclerView.
                it?.forEachIndexed { _, location ->
                    location.flag = false
                }
                locationAdapter.submitList(it)
            }
            findNavController().navigate(R.id.action_MainFragment_to_LocationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}