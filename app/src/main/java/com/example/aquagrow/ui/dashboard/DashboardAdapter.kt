package com.example.aquagrow.ui.dashboard

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import android.view.LayoutInflater
import com.example.aquagrow.data.model.domain.Telemetry

class DashboardAdapter(
    private var units : List<com.example.aquagrow.data.model.domain.Unit>,
    private val onSelectItem : (com.example.aquagrow.data.model.domain.Unit) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.UnitViewHolder>() {

    inner class UnitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUnitName: TextView = view.findViewById(R.id.tvUnitName)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
        val tvHumidity: TextView = view.findViewById(R.id.tvHumidity)
        val tvPh: TextView = view.findViewById(R.id.tvPh)

        init {
            view.apply {
                isClickable = true
                setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        onSelectItem(units[position])
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_unit, parent, false)
        return UnitViewHolder(view)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        val unit = units[position]
        holder.tvUnitName.text = unit.nombre

        val telem = telemetryMap[unit.id_unidad]
        holder.tvTemperature.text = "${telem?.tempAgua ?: "--"}°C"
        holder.tvHumidity.text = "${telem?.tempZona ?: "--"}°C"
        holder.tvPh.text = "${telem?.ph ?: "--"}"
    }

    override fun getItemCount(): Int = units.size

    fun updateList(newList: List<com.example.aquagrow.data.model.domain.Unit>) {
        units = newList
        notifyDataSetChanged()
    }

    private var telemetryMap: Map<Int, Telemetry> = emptyMap()

    fun updateTelemetryMap(newMap: Map<Int, Telemetry>) {
        telemetryMap = newMap
        notifyDataSetChanged()
    }
}