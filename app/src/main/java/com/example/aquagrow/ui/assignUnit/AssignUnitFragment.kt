package com.example.aquagrow.ui.assignUnit

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import kotlinx.coroutines.launch

class AssignUnitFragment : Fragment() {

    private val sharedViewModel: AdminUnitViewModel by activityViewModels()
    private val formViewModel: AssignUnitFormViewModel by activityViewModels()

    private lateinit var etUnitName: EditText
    private lateinit var etZoneName: EditText
    private lateinit var etTankName: EditText
    private lateinit var btnSave: Button
    private lateinit var tvUserName: TextView
    private lateinit var btnRemoveUser: Button
    private lateinit var btnSearchUser: Button
    private lateinit var assignedUserSection: LinearLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_assign_unit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etUnitName = view.findViewById(R.id.etUnitName)
        etZoneName = view.findViewById(R.id.etZoneName)
        etTankName = view.findViewById(R.id.etTankName)
        btnSave = view.findViewById(R.id.btnSaveChanges)
        tvUserName = view.findViewById(R.id.tvUserName)
        btnRemoveUser = view.findViewById(R.id.btnRemoveUser)
        btnSearchUser = view.findViewById(R.id.btnSearchUser)
        assignedUserSection = view.findViewById(R.id.assignedUserSection)
        progressBar = view.findViewById(R.id.progressBar)

        // Cargar unidad seleccionada desde el ViewModel compartido
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                sharedViewModel.selectedUnit.collect { unit ->
                    unit?.let { formViewModel.loadUnit(it) }
                }
            }
        }

        observeState()

        btnSave.setOnClickListener {
            val name = etUnitName.text.toString().trim()
            val zone = etZoneName.text.toString().trim()
            val tank = etTankName.text.toString().trim()

            if (name.isNotEmpty()) {
                formViewModel.updateUnit(name, zone, tank)
            } else {
                showToast("El nombre de unidad es obligatorio")
            }
        }

        btnRemoveUser.setOnClickListener {
            formViewModel.removeUser()
        }

        btnSearchUser.setOnClickListener {
            UserSearchDialogFragment { selectedUser ->
                formViewModel.assignUser(selectedUser)
            }.show(parentFragmentManager, "UserSearchDialog")
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                formViewModel.state.collect { state ->
                    when (state) {
                        is AssignUnitFormState.Loading -> showLoading(true)
                        is AssignUnitFormState.Loaded -> {
                            showLoading(false)
                            populateFields(state.unit)
                        }
                        is AssignUnitFormState.UpdateSuccess -> {
                            showLoading(false)
                            showToast("Cambios guardados")
                            formViewModel.currentUnit?.let { populateFields(it) }
                        }
                        is AssignUnitFormState.RemoveUserSuccess -> {
                            showLoading(false)
                            showToast("Usuario quitado")
                            // Actualizar vista
                            formViewModel.currentUnit?.let { formViewModel.loadUnit(it.copy(user = null)) }
                        }
                        is AssignUnitFormState.Error -> {
                            showLoading(false)
                            showToast("${state.message}")
                        }
                    }
                }
            }
        }
    }

    private fun populateFields(unit: com.example.aquagrow.data.model.domain.Unit) {
        etUnitName.setText(unit.nombre)
        etZoneName.setText(unit.zone?.nombre ?: "")
        etTankName.setText(unit.tank?.nombre ?: "")

        if (unit.user != null) {
            tvUserName.text = unit.user.usuario
            assignedUserSection.visibility = View.VISIBLE
            btnSearchUser.visibility = View.GONE
        } else {
            assignedUserSection.visibility = View.GONE
            btnSearchUser.visibility = View.VISIBLE
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(show: Boolean) {
        progressBar?.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
        btnRemoveUser.isEnabled = !show
        btnSearchUser.isEnabled = !show
    }
}
