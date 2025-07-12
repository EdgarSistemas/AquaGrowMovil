// AdminUnitListFragment.kt
package com.example.aquagrow.ui.admin.units

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.*
import com.example.aquagrow.R
import com.example.aquagrow.ui.assignUnit.AdminUnitAdapter
import com.example.aquagrow.ui.assignUnit.AdminUnitViewModel
import com.example.aquagrow.ui.assignUnit.AssignUnitFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AdminUnitListFragment : Fragment() {

    private val viewModel: AdminUnitViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: AdminUnitAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_admin_unit_list, container, false)
        recyclerView = view.findViewById(R.id.rvAdminUnits)
        progressBar = view.findViewById(R.id.progressBarAdminUnits)
        tvEmptyState = view.findViewById(R.id.tvEmptyAdminUnits)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = AdminUnitAdapter(emptyList()) { unit ->
            viewModel.selectUnit(unit)
            navigateToAssignFragment()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.units.collect { units ->
                        adapter.updateList(units)
                        recyclerView.visibility = if (units.isNotEmpty()) View.VISIBLE else View.GONE
                        tvEmptyState.visibility = if (units.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.errorMessage.collect { msg ->
                        msg?.let {
                            Snackbar.make(view, it, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        if (viewModel.units.value.isEmpty()) {
            viewModel.loadAllUnits()
        }
    }

    private fun navigateToAssignFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AssignUnitFragment())
            .addToBackStack(null)
            .commit()
    }
}
