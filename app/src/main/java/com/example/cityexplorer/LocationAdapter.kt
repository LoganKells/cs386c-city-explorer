package com.example.cityexplorer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.cityexplorer.api.Location
import com.example.cityexplorer.databinding.RowBinding

class LocationAdapter(private val viewModel: MainViewModel)
    : ListAdapter<Location, LocationAdapter.ViewHolder>(LocationDiffCallback()) {

    inner class ViewHolder(val rowBinding: RowBinding)
        : RecyclerView.ViewHolder(rowBinding.root) {
        init {
            // Note - Not doing anything here, instead see onBindViewHolder().
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowBinding = RowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Get the data for the Location row in this position
        val location: Location = getItem(position)

        // Populate the data in the row.xml layout.
        holder.itemView.apply {
            // REVIEW - Is this what we want users to see?
            val address = "${location.address1}, ${location.address2}, ${location.city}, ${location.state}"

            // Populate text values.
            holder.rowBinding.rowTextViewName.text = location.nickname
            holder.rowBinding.rowTextViewAddress.text = address
            holder.rowBinding.rowTextViewRating.text = location.rating.toString()

            holder.rowBinding.rowCheckBoxDelete.setOnClickListener {
                Log.d(javaClass.simpleName, "PREV"+location.flag)
                location.flag = location.flag != true
                Log.d(javaClass.simpleName, "AFTER"+location.flag)
            }
        }
    }

    // FIXME - Why can we have multiple locations with the same values?
    class LocationDiffCallback : DiffUtil.ItemCallback<Location>() {
        override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
            return oldItem == newItem
        }
    }

}
