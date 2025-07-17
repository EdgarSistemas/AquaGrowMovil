package com.example.aquagrow.ui.unit.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aquagrow.R
import com.example.aquagrow.data.model.domain.Unit
import com.example.aquagrow.ui.assignUnit.AdminUnitViewModel
import com.example.aquagrow.ui.growing.GrowingFormFragment
import com.example.aquagrow.ui.unit.UnitDetailState
import com.example.aquagrow.ui.unit.UnitDetailViewModel
import com.example.aquagrow.ui.configZone.ConfigZoneFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class UnitDetailFragment : Fragment() {

    private val viewModel: UnitDetailViewModel by viewModels()
    private val adminUnitViewModel: AdminUnitViewModel by activityViewModels()

    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    private lateinit var cardCultivo: CardView
    private lateinit var cardZona: CardView
    private lateinit var cardEstanque: CardView

    private lateinit var tvCultivo: TextView
    private lateinit var tvZona: TextView
    private lateinit var tvEstanque: TextView

    companion object {
        private const val ARG_UNIT_ID = "unit_id"

        fun newInstance(unitId: Int): UnitDetailFragment {
            val fragment = UnitDetailFragment()
            val args = Bundle()
            args.putInt(ARG_UNIT_ID, unitId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_unit_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Referencias UI
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.errorText)
        cardCultivo = view.findViewById(R.id.cardCultivo)
        cardZona = view.findViewById(R.id.cardZona)
        cardEstanque = view.findViewById(R.id.cardEstanque)
        tvCultivo = view.findViewById(R.id.tvCultivo)
        tvZona = view.findViewById(R.id.tvZona)
        tvEstanque = view.findViewById(R.id.tvEstanque)

        // Obtener ID
        val unitId = arguments?.getInt(ARG_UNIT_ID) ?: return

        // Cargar datos
        viewModel.loadUnitDetail(unitId)

        // Observar estado
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is UnitDetailState.Loading -> showLoading()
                    is UnitDetailState.Error -> showError(state.message)
                    is UnitDetailState.Success -> showData(state.unit)
                }
            }
        }

        cardCultivo.setOnClickListener {
            val fragment = GrowingFormFragment.newInstance(unitId)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        cardZona.setOnClickListener {
            (viewModel.state.value as? UnitDetailState.Success)?.unit?.let { unit ->
                adminUnitViewModel.selectUnit(unit)
                val fragment = ConfigZoneFragment.newInstance(unit.id_unidad)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun showLoading() {
        progressBar.isVisible = true
        errorText.isVisible = false
        cardCultivo.isVisible = false
        cardZona.isVisible = false
        cardEstanque.isVisible = false
    }

    private fun showError(message: String) {
        progressBar.isVisible = false
        errorText.isVisible = true
        errorText.text = message
        cardCultivo.isVisible = false
        cardZona.isVisible = false
        cardEstanque.isVisible = false
    }

    private fun showData(unit: Unit) {
        progressBar.isVisible = false
        errorText.isVisible = false

        // Cultivo actual
        cardCultivo.isVisible = true
        unit.growing?.let {
            val fechaInicio = parseDate(it.fecha_inicio)
            val dias = it.dias_esperados?.takeIf { d -> d > 0 } ?: 0
            tvCultivo.text = "Nombre de la planta: ${it.nombre_planta} \nInicio de cultivo: $fechaInicio\nDías esperados: $dias"
        } ?: run {
            tvCultivo.text = "Sin cultivo activo"
        }

        // Zona de cultivo
        cardZona.isVisible = true
        tvZona.text = unit.zone?.nombre ?: "Zona no disponible"

        // Estanque
        cardEstanque.isVisible = true
        tvEstanque.text = unit.tank?.nombre ?: "Estanque no disponible"
    }

    private fun parseDate(dateStr: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateStr ?: "")
            if (date != null) outputFormat.format(date) else "Fecha desconocida"
        } catch (e: Exception) {
            "Fecha inválida"
        }
    }
}
