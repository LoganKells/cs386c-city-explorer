package com.example.cityexplorer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val data: ArrayList<Location>) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var nameView: TextView = view.findViewById(R.id.rowTextViewName) as TextView
        var ratingView: TextView = view.findViewById(R.id.rowTextViewRating) as TextView
        var addressView: TextView = view.findViewById(R.id.rowTextViewAddress) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nameView.text = data[position].name
        holder.ratingView.text = data[position].rating
        holder.addressView.text =data[position].address
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
