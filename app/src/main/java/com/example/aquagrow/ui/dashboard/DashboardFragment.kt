package com.example.aquagrow.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.example.aquagrow.R
import com.example.aquagrow.data.model.domain.Unit
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private lateinit var adapter: DashboardAdapter
    private lateinit var rvUnits: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var fabRefresh: FloatingActionButton

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Inicializar vistas
        rvUnits = view.findViewById(R.id.rvUnits)
        progressBar = view.findViewById(R.id.progressBar)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)
        fabRefresh = view.findViewById(R.id.fabRefresh)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DashboardFragment", "🚀 onViewCreated")
        rvUnits.layoutAnimation = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_fall_down)

        setupObservers()
        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserUnits()
        viewModel.ensureMqttTelemetryObserver()
    }

    private fun setupRecyclerView() {
        adapter = DashboardAdapter(
            emptyList(),
            onSelectItem = {unit -> navigateToUnitFragment(unit)}
        )
        rvUnits.layoutManager = LinearLayoutManager(requireContext())
        rvUnits.adapter = adapter
    }

    private fun setupListeners() {
        fabRefresh.setOnClickListener {
            viewModel.loadUserUnits()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observar estado de carga de unidades
                launch {
                    viewModel.state.collect { state ->
                        when (state) {
                            DashboardState.Loading -> showLoading(true)
                            is DashboardState.Success -> {
                                showLoading(false)
                                if (state.units.isNotEmpty()) {
                                    adapter.updateList(state.units)
                                    rvUnits.visibility = View.VISIBLE
                                    tvEmptyState.visibility = View.GONE
                                } else {
                                    rvUnits.visibility = View.GONE
                                    tvEmptyState.visibility = View.VISIBLE
                                }
                            }
                            is DashboardState.Error -> {
                                showLoading(false)
                                showError(state.message)
                            }
                            DashboardState.Empty -> {
                                showLoading(false)
                                rvUnits.visibility = View.GONE
                                tvEmptyState.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                // Observar datos de telemetría por unidad
                launch {
                    viewModel.telemetries.collect { telemetries ->
                        adapter.updateTelemetryMap(telemetries)
                    }
                }
            }
        }
    }

    private fun navigateToUnitFragment(unit: Unit) {
        // Mostrar Toast para prueba rápida
        Toast.makeText(requireContext(), "Click en ${unit.nombre}", Toast.LENGTH_SHORT).show()

        // O usando Snackbar seguro
        Snackbar.make(requireView(), "Unidad ${unit.id_unidad} seleccionada", Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.teal_700))
            .show()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        rvUnits.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun showSuccess(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.teal_700))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                .show()
        }
    }

    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.black))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .setAction("Reintentar") { viewModel.loadUserUnits() }
                .show()
        }
    }
}