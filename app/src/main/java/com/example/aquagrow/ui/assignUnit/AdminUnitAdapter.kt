package com.example.aquagrow.ui.assignUnit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R

class AdminUnitAdapter(
    private var units: List<com.example.aquagrow.data.model.domain.Unit>,
    private val onClick: (com.example.aquagrow.data.model.domain.Unit) -> Unit
) : RecyclerView.Adapter<AdminUnitAdapter.UnitViewHolder>() {

    inner class UnitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUnitName: TextView = view.findViewById(R.id.tvUnitName)

        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onClick(units[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_unit_admin, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.tvUnitName.text = units[position].nombre
    }

    override fun getItemCount(): Int = units.size

    fun updateList(newList: List<com.example.aquagrow.data.model.domain.Unit>) {
        units = newList
        notifyDataSetChanged()
    }
}
