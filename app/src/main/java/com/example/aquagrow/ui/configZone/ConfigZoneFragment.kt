package com.example.aquagrow.ui.configZone

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import com.example.aquagrow.ui.zone.ZoneConfigState
import com.example.aquagrow.ui.zone.ZoneConfigViewModel


class ConfigZoneFragment : Fragment() {

    companion object {
        fun newInstance(unitId: Int): ConfigZoneFragment {
            val fragment = ConfigZoneFragment()
            val args = Bundle()
            args.putInt("unit_id", unitId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var formViewModel: ZoneConfigViewModel

    private lateinit var etTempMin: EditText
    private lateinit var etTempMax: EditText
    private lateinit var etHoraInicio: EditText
    private lateinit var etDuracion: EditText
    private lateinit var etFrecuenciaDia: EditText
    private lateinit var etIntervalo: EditText
    private lateinit var spinnerTipoFrecuencia: Spinner
    private lateinit var btnGuardar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var checkboxesDias: List<CheckBox>
    private lateinit var tvTempZona: TextView
    private lateinit var scrollContainer: NestedScrollView
    private lateinit var switchBomba: Switch
    private lateinit var switchVentilador: Switch
    private lateinit var switchLuces: Switch

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_config_zone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        formViewModel = ZoneConfigViewModel(
            repository = com.example.aquagrow.data.repository.ConfigZoneRepository(),
            mqttManager = MqttClientManager
        )

        etTempMin = view.findViewById(R.id.etTempMin)
        etTempMax = view.findViewById(R.id.etTempMax)
        etHoraInicio = view.findViewById(R.id.etHoraInicio)
        etDuracion = view.findViewById(R.id.etDuracion)
        etFrecuenciaDia = view.findViewById(R.id.etFrecuenciaDia)
        etIntervalo = view.findViewById(R.id.etIntervalo)
        spinnerTipoFrecuencia = view.findViewById(R.id.spinnerTipoFrecuencia)
        btnGuardar = view.findViewById(R.id.btnGuardarConfig)
        progressBar = view.findViewById(R.id.progressBar)
        tvTempZona = view.findViewById(R.id.tvTempZona)
        scrollContainer = view.findViewById(R.id.scrollContainer)
        switchBomba = view.findViewById(R.id.switchBomba)
        switchVentilador = view.findViewById(R.id.switchVentilador)
        switchLuces = view.findViewById(R.id.switchLuces)

        checkboxesDias = listOf(
            view.findViewById(R.id.cbLunes),
            view.findViewById(R.id.cbMartes),
            view.findViewById(R.id.cbMiercoles),
            view.findViewById(R.id.cbJueves),
            view.findViewById(R.id.cbViernes),
            view.findViewById(R.id.cbSabado),
            view.findViewById(R.id.cbDomingo)
        )

        scrollContainer.setPadding(
            scrollContainer.paddingLeft,
            scrollContainer.paddingTop,
            scrollContainer.paddingRight,
            scrollContainer.paddingBottom + 100
        )

        scrollContainer.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.tipo_frecuencia_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipoFrecuencia.adapter = adapter
        }

        spinnerTipoFrecuencia.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                val habilitar = selected == "Días de semana"
                checkboxesDias.forEach { it.isEnabled = habilitar }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val unitId = arguments?.getInt("unit_id") ?: return@launch
            val unit = formViewModel.getFullUnitFromApi(unitId)
            if (unit != null) {
                Log.d("ZoneConfig", "Unidad completa recibida: ${unit.id_unidad}")
                val zonaId = unit.zone?.id_zona ?: return@launch
                val unidadId = unit.id_unidad
                val dispId = unit.dispositivo?.id_dispositivo ?: return@launch
                formViewModel.setIdentifiers(zonaId, unidadId, dispId)

                val configId = unit.zone?.configZone?.id_config
                val progId = unit.zone?.programacionRiego?.id_programacion
                formViewModel.loadData(zonaId, progId)
            } else {
                Log.e("ZoneConfig", "Unidad no encontrada en servidor")
            }
        }

        observeState()

        btnGuardar.setOnClickListener {
            val selectedDias = checkboxesDias.filter { it.isChecked }.map {
                when (it.id) {
                    R.id.cbLunes -> "1"
                    R.id.cbMartes -> "2"
                    R.id.cbMiercoles -> "3"
                    R.id.cbJueves -> "4"
                    R.id.cbViernes -> "5"
                    R.id.cbSabado -> "6"
                    R.id.cbDomingo -> "7"
                    else -> ""
                }
            }.joinToString(",")

            formViewModel.saveConfigZone(
                tempMin = etTempMin.text.toString().toDoubleOrNull() ?: 0.0,
                tempMax = etTempMax.text.toString().toDoubleOrNull() ?: 0.0
            )
            formViewModel.saveIrrigationSchedule(
                horaInicio = etHoraInicio.text.toString(),
                duracionMinutos = etDuracion.text.toString().toIntOrNull() ?: 0,
                tipoFrecuencia = spinnerTipoFrecuencia.selectedItem.toString(),
                diasSemana = selectedDias,
                frecuenciaDia = etFrecuenciaDia.text.toString().toIntOrNull() ?: 0,
                intervaloMinutos = etIntervalo.text.toString().toIntOrNull() ?: 0
            )
        }

        switchBomba.setOnCheckedChangeListener { _, isChecked ->
            formViewModel.activateActuator("bomba", isChecked)
        }

        switchVentilador.setOnCheckedChangeListener { _, isChecked ->
            formViewModel.activateActuator("ventilador", isChecked)
        }

        switchLuces.setOnCheckedChangeListener { _, isChecked ->
            formViewModel.activateActuator("luz", isChecked)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                formViewModel.state.collect { state ->
                    Log.d("ZoneConfig", "Estado observado: $state")
                    when (state) {
                        is ZoneConfigState.Loading -> showLoading(true)
                        is ZoneConfigState.Loaded -> {
                            showLoading(false)
                            populateFields(state)
                            scrollContainer.visibility = View.VISIBLE
                            formViewModel.subscribeToTelemetry(tvTempZona)
                        }
                        is ZoneConfigState.Success -> {
                            showLoading(false)
                            showSnackbar(state.mensaje)
                        }
                        is ZoneConfigState.Error -> {
                            showLoading(false)
                            showSnackbar("Error: ${state.mensaje}")
                        }
                    }
                }
            }
        }
    }

    private fun populateFields(state: ZoneConfigState.Loaded) {
        state.configZone?.let {
            etTempMin.setText(it.temp_min.toString())
            etTempMax.setText(it.temp_max.toString())
        }

        state.programacionRiego?.let {
            etHoraInicio.setText(it.hora_inicio)
            etDuracion.setText(it.duracion_minutos.toString())
            etFrecuenciaDia.setText(it.frecuencia_dia.toString())
            etIntervalo.setText(it.intervalo_minutos.toString())
            val index = resources.getStringArray(R.array.tipo_frecuencia_array).indexOf(it.tipo_frecuencia)
            if (index >= 0) spinnerTipoFrecuencia.setSelection(index)

            val dias = it.dias_semana.split(",").map { dia -> dia.trim() }
            val mapaDias = mapOf("1" to R.id.cbLunes, "2" to R.id.cbMartes, "3" to R.id.cbMiercoles, "4" to R.id.cbJueves,
                "5" to R.id.cbViernes, "6" to R.id.cbSabado, "7" to R.id.cbDomingo)
            dias.forEach { dia ->
                mapaDias[dia]?.let { id -> view?.findViewById<CheckBox>(id)?.isChecked = true }
            }
        }
    }

    private fun showSnackbar(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.success))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .show()
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnGuardar.isEnabled = !show
        switchBomba.isEnabled = !show
        switchVentilador.isEnabled = !show
        switchLuces.isEnabled = !show
    }
}
