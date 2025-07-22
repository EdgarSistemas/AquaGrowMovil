package com.example.aquagrow.ui.growing

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.aquagrow.R
import com.example.aquagrow.data.model.domain.Growing
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class GrowingFormFragment : Fragment() {

    private val viewModel: GrowingFormViewModel by viewModels()
    private var unitId: Int = -1

    private lateinit var etNombrePlanta: EditText
    private lateinit var etDiasEsperados: EditText
    private lateinit var etRendimientoEsperado: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnTerminar: Button

    private lateinit var progressBar: ProgressBar

    private lateinit var dialogView: View
    private lateinit var spinnerEstatus: Spinner
    private lateinit var etRendimientoReal: EditText
    private lateinit var etMotivo: EditText
    private lateinit var etDiasReal: EditText

    companion object {
        private const val ARG_UNIT_ID = "unit_id"

        fun newInstance(unitId: Int): GrowingFormFragment {
            val fragment = GrowingFormFragment()
            val args = Bundle()
            args.putInt(ARG_UNIT_ID, unitId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_growing_form, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        try {
            unitId = arguments?.getInt(ARG_UNIT_ID) ?: return

            etNombrePlanta = view.findViewById(R.id.etNombrePlanta)
            etDiasEsperados = view.findViewById(R.id.etDiasEsperados)
            etRendimientoEsperado = view.findViewById(R.id.etRendimientoEsperado)
            btnGuardar = view.findViewById(R.id.btnGuardarCultivo)
            btnTerminar = view.findViewById(R.id.btnTerminarCultivo)
            progressBar = view.findViewById(R.id.progressBarGrowing)

            btnGuardar.setOnClickListener {
                val dias = etDiasEsperados.text.toString().toIntOrNull() ?: 0
                val rendimiento = etRendimientoEsperado.text.toString().toIntOrNull() ?: 0
                val nombre = etNombrePlanta.text.toString()

                val current = viewModel.state.value
                if (current is GrowingFormState.Form && current.growing != null) {
                    viewModel.actualizarCultivo(unitId, current.growing.id_cultivo, nombre, dias, rendimiento)
                } else {
                    viewModel.guardarNuevoCultivo(unitId, nombre, dias, rendimiento)
                }
            }

            btnTerminar.setOnClickListener {
                mostrarDialogoTerminarCultivo()
            }

            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    when (state) {
                        is GrowingFormState.Loading -> mostrarCargando()
                        is GrowingFormState.Error -> showError(state.message)
                        is GrowingFormState.Form -> {
                            mostrarFormulario(state.growing)
                        }
                        is GrowingFormState.Success -> {
                            showSuccess("Operación exitosa")
                            view?.postDelayed({
                                activity?.onBackPressedDispatcher?.onBackPressed()
                            }, 1000)
                        }
                    }
                }
            }

            viewModel.cargarCultivo(unitId)
        } catch (e : Exception) {

        }
    }

    private fun mostrarCargando() {
        progressBar.isVisible = true
    }

    private fun mostrarFormulario(g: Growing?) {
        progressBar.isVisible = false

        if (g != null) {
            etNombrePlanta.setText(g.nombre_planta)
            etDiasEsperados.setText(g.dias_esperados.toString())
            etRendimientoEsperado.setText(g.rendimiento_esperado.toString())
            btnTerminar.isVisible = true
        } else {
            btnTerminar.isVisible = false
        }
    }

    private fun mostrarDialogoTerminarCultivo() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_terminar_cultivo, null)
        spinnerEstatus = dialogView.findViewById(R.id.spinnerEstatusFinal)
        etRendimientoReal = dialogView.findViewById(R.id.etRendimientoReal)
        etDiasReal = dialogView.findViewById(R.id.etDiasReal)
        etMotivo = dialogView.findViewById(R.id.etMotivo)

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.estatus_final_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstatus.adapter = adapter

        builder.setView(dialogView)
            .setTitle("Terminar cultivo")
            .setPositiveButton("Aceptar") { _, _ ->
                val status = when (spinnerEstatus.selectedItem.toString()) {
                    "Completado" -> 1
                    "Fallido" -> 2
                    "Cancelado" -> 3
                    else -> 0
                }

                val diasReal = etDiasReal.text.toString().toIntOrNull() ?: 0
                val rendimientoReal = etRendimientoReal.text.toString().toIntOrNull() ?: 0
                val motivo = etMotivo.text.toString()
                val idCultivo = (viewModel.state.value as? GrowingFormState.Form)?.growing?.id_cultivo ?: return@setPositiveButton

                viewModel.terminarCultivo(unitId, idCultivo, motivo, diasReal, rendimientoReal, status)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showSuccess(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)

        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.success))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setAnchorView(bottomNav)
            .show()
    }

    private fun showError(message: String) {
        val rootView = requireActivity().findViewById<View>(android.R.id.content)
        val bottomNav = requireActivity().findViewById<View>(R.id.bottom_navigation)

        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .setAnchorView(bottomNav)
            .show()
    }
}
