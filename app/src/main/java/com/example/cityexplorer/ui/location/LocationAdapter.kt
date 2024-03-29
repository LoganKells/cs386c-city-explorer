package com.example.cityexplorer.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityexplorer.ui.main.MainViewModel
import com.example.cityexplorer.data.Location
import com.example.cityexplorer.databinding.RowBinding

class LocationAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Location, LocationAdapter.ViewHolder>(LocationDiffCallback()) {

    inner class ViewHolder(val rowBinding: RowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {
            // Note - Not doing anything here, instead see onBindViewHolder().
        }
    }

    private lateinit var rowBinding: RowBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        rowBinding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data for the Location row in this position
        val locationInAdapter: Location = getItem(position)

        // Populate the data in the row.xml layout.
        holder.itemView.apply {
            // REVIEW - Is this what we want users to see?
            val address = "${locationInAdapter.address1} ${locationInAdapter.address2}, ${locationInAdapter.city}, ${locationInAdapter.state} ${locationInAdapter.postCode}, ${locationInAdapter.country}"

            // Populate text values.
            holder.rowBinding.rowTextViewName.text = locationInAdapter.nickname
            holder.rowBinding.rowTextViewAddress.text = address
            holder.rowBinding.rowTextViewRating.text = locationInAdapter.rating.toString()

            // Ensure the checkbox is set to the correct value.
            holder.rowBinding.rowCheckBoxDelete.isChecked = locationInAdapter.deleteFlag

            // Highlight the row of the starting location with a symbol.
            if (locationInAdapter.startFlag) {
                holder.rowBinding.rowTextViewStartLocation.visibility = View.VISIBLE
            } else {
                holder.rowBinding.rowTextViewStartLocation.visibility = View.INVISIBLE
            }

            // Update the deleteFlag in the Location object when the checkbox is clicked in the
            // view model.
            holder.rowBinding.rowCheckBoxDelete.setOnClickListener {
                locationInAdapter.deleteFlag = locationInAdapter.deleteFlag != true
                viewModel.updateLocation(locationInAdapter, position)
            }

            // Go to the maps screen when the user clicks on the row.
            holder.rowBinding.root.setOnClickListener {
                viewModel.onLocationClicked(locationInAdapter)
            }
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }

}
