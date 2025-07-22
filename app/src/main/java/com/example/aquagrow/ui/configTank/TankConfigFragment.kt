package com.example.aquagrow.ui.configTank

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import com.example.aquagrow.data.repository.TankConfigRepository
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TankConfigFragment : Fragment() {

    companion object {
        fun newInstance(unitId: Int): TankConfigFragment {
            val fragment = TankConfigFragment()
            val args = Bundle()
            args.putInt("unit_id", unitId)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: TankConfigViewModel

    private lateinit var etTempMin: EditText
    private lateinit var etTempMax: EditText
    private lateinit var etPhMin: EditText
    private lateinit var etPhMax: EditText
    private lateinit var etDistMin: EditText
    private lateinit var etDistMax: EditText

    private lateinit var etHoraInicio: EditText
    private lateinit var etDuracion: EditText
    private lateinit var etFrecuenciaDia: EditText
    private lateinit var etIntervalo: EditText
    private lateinit var spinnerTipoFrecuencia: Spinner
    private lateinit var checkboxesDias: List<CheckBox>

    private lateinit var btnSaveConfig: Button
    private lateinit var btnSaveFeeding: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollContainer: NestedScrollView
    private lateinit var switchAlimentador: Switch
    private lateinit var tvTempAgua: TextView
    private lateinit var tvPhAgua: TextView
    private lateinit var tvNivelAgua: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tank_config, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = TankConfigViewModel(
            repository = TankConfigRepository(),
            mqttManager = MqttClientManager
        )

        etTempMin = view.findViewById(R.id.etTempMin)
        etTempMax = view.findViewById(R.id.etTempMax)
        etPhMin = view.findViewById(R.id.etPhMin)
        etPhMax = view.findViewById(R.id.etPhMax)
        etDistMin = view.findViewById(R.id.etDistMin)
        etDistMax = view.findViewById(R.id.etDistMax)

        etHoraInicio = view.findViewById(R.id.etHoraInicio)
        etDuracion = view.findViewById(R.id.etDuracion)
        etFrecuenciaDia = view.findViewById(R.id.etFrecuenciaDia)
        etIntervalo = view.findViewById(R.id.etIntervalo)
        spinnerTipoFrecuencia = view.findViewById(R.id.spinnerTipoFrecuencia)

        btnSaveConfig = view.findViewById(R.id.btnSaveConfig)
        btnSaveFeeding = view.findViewById(R.id.btnSaveFeeding)
        progressBar = view.findViewById(R.id.progressBar)
        scrollContainer = view.findViewById(R.id.scrollContainer)
        switchAlimentador = view.findViewById(R.id.switchAlimentador)
        tvTempAgua = view.findViewById(R.id.tvTempAgua)
        tvPhAgua = view.findViewById(R.id.tvPhAgua)
        tvNivelAgua = view.findViewById(R.id.tvNivelAgua)

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
            override fun onItemSelected(p: AdapterView<*>, view: View?, pos: Int, id: Long) {
                val selected = p.getItemAtPosition(pos).toString()
                val enabled = selected == "Días de semana"
                checkboxesDias.forEach { it.isEnabled = enabled }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        val unitId = arguments?.getInt("unit_id") ?: return

        lifecycleScope.launch {
            val unit = viewModel.getFullUnitFromApi(unitId)
            if (unit != null) {
                val estanqueId = unit.tank?.id_estanque ?: return@launch
                val programacionId = unit.tank?.programacionAlimentacion?.id_programacion
                val dispId = unit.dispositivo?.id_dispositivo ?: return@launch

                viewModel.setIdentifiers(estanqueId, programacionId, unitId, dispId)
                viewModel.loadData(estanqueId, programacionId)
            }
        }

        btnSaveConfig.setOnClickListener {
            viewModel.saveConfigTank(
                tempMin = etTempMin.text.toString().toDoubleOrNull() ?: 0.0,
                tempMax = etTempMax.text.toString().toDoubleOrNull() ?: 0.0,
                phMin = etPhMin.text.toString().toDoubleOrNull() ?: 0.0,
                phMax = etPhMax.text.toString().toDoubleOrNull() ?: 0.0,
                distMin = etDistMin.text.toString().toDoubleOrNull() ?: 0.0,
                distMax = etDistMax.text.toString().toDoubleOrNull() ?: 0.0
            )
        }

        btnSaveFeeding.setOnClickListener {
            val selectedDias = checkboxesDias.filter { it.isChecked }.map {
                when (it.id) {
                    R.id.cbLunes -> "lunes"
                    R.id.cbMartes -> "martes"
                    R.id.cbMiercoles -> "miercoles"
                    R.id.cbJueves -> "jueves"
                    R.id.cbViernes -> "viernes"
                    R.id.cbSabado -> "sabado"
                    R.id.cbDomingo -> "domingo"
                    else -> ""
                }
            }.joinToString(",").ifEmpty { null }

            viewModel.saveFeedingSchedule(
                horaInicio = etHoraInicio.text.toString(),
                duracionMinutos = etDuracion.text.toString().toIntOrNull() ?: 0,
                tipoFrecuencia = spinnerTipoFrecuencia.selectedItem.toString(),
                diasSemana = selectedDias,
                frecuenciaDia = etFrecuenciaDia.text.toString().toIntOrNull(),
                intervaloMinutos = etIntervalo.text.toString().toIntOrNull()
            )
        }

        switchAlimentador.setOnCheckedChangeListener { _, isChecked ->
            viewModel.activateActuator("alimentador", isChecked)
        }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is TankConfigState.Loading -> showLoading(true)
                        is TankConfigState.Loaded -> {
                            showLoading(false)
                            scrollContainer.visibility = View.VISIBLE
                            populateFields(state)
                            viewModel.subscribeToTelemetry(tvTempAgua, tvPhAgua, tvNivelAgua)
                        }
                        is TankConfigState.Success -> {
                            showLoading(false)
                            showSnackbar(state.mensaje)
                        }
                        is TankConfigState.Error -> {
                            showLoading(false)
                            showSnackbar("Error: ${state.mensaje}")
                        }
                    }
                }
            }
        }
    }

    private fun populateFields(state: TankConfigState.Loaded) {
        state.configTank?.let {
            etTempMin.setText(it.temp_agua_min.toString())
            etTempMax.setText(it.temp_agua_max.toString())
            etPhMin.setText(it.ph_min.toString())
            etPhMax.setText(it.ph_max.toString())
            etDistMin.setText(it.dist_min.toString())
            etDistMax.setText(it.dist_max.toString())
        }

        state.feedingSchedule?.let {
            etHoraInicio.setText(it.hora_inicio)
            etDuracion.setText(it.duracion_minutos.toString())
            etFrecuenciaDia.setText(it.frecuencia_dia?.toString() ?: "")
            etIntervalo.setText(it.intervalo_minutos?.toString() ?: "")
            val index = resources.getStringArray(R.array.tipo_frecuencia_array).indexOf(it.tipo_frecuencia)
            if (index >= 0) spinnerTipoFrecuencia.setSelection(index)

            checkboxesDias.forEach { it.isChecked = false }
            it.dias_semana?.split(",")?.map { dia -> dia.trim().lowercase() }?.forEach { dia ->
                when (dia) {
                    "lunes" -> checkboxesDias[0].isChecked = true
                    "martes" -> checkboxesDias[1].isChecked = true
                    "miercoles" -> checkboxesDias[2].isChecked = true
                    "jueves" -> checkboxesDias[3].isChecked = true
                    "viernes" -> checkboxesDias[4].isChecked = true
                    "sabado" -> checkboxesDias[5].isChecked = true
                    "domingo" -> checkboxesDias[6].isChecked = true
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSaveConfig.isEnabled = !show
        btnSaveFeeding.isEnabled = !show
        switchAlimentador.isEnabled = !show
    }

    private fun showSnackbar(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)

        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.success))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setAnchorView(bottomNav)
            .show()
    }
}
