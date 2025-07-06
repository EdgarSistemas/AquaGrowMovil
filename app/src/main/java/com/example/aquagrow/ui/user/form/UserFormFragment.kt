package com.example.aquagrow.ui.user.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import com.example.aquagrow.data.model.domain.UserType
import com.example.aquagrow.data.model.responses.UserComReponse
import com.example.aquagrow.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class UserFormFragment : Fragment() {
    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Int): UserFormFragment {
            val fragment = UserFormFragment()
            val args = Bundle()
            args.putInt(ARG_USER_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: UserFormViewModel by viewModels()
    private var userId = 0

    private lateinit var tvTitle: TextView
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var lyPassword : TextInputLayout
    private lateinit var etPassword: TextInputEditText
    private lateinit var lyConfirmPassword : TextInputLayout
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var spinnerUserType: Spinner
    private lateinit var cbChangePassword: CheckBox
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    private var userTypes: List<UserType> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_form, container, false)

        // Initialize views
        tvTitle = view.findViewById(R.id.tvTitle)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etUsername = view.findViewById(R.id.etUsername)
        lyPassword = view.findViewById(R.id.lyPassword)
        etPassword = view.findViewById(R.id.etPassword)
        lyConfirmPassword = view.findViewById(R.id.lyConfirmPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        spinnerUserType = view.findViewById(R.id.spinnerUserType)
        cbChangePassword = view.findViewById(R.id.cbChangePassword)
        btnSave = view.findViewById(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt(ARG_USER_ID, 0) ?: 0

        setupUI()
        setupObservers()

        if (userId > 0) {
            tvTitle.text = "Editar usuario"
            viewModel.loadUserData(userId)
            cbChangePassword.visibility = View.VISIBLE
            spinnerUserType.visibility = View.GONE
            lyPassword.visibility = View.GONE
            etPassword.visibility = View.GONE
            lyConfirmPassword.visibility = View.GONE
            etConfirmPassword.visibility = View.GONE
        } else {
            tvTitle.text = "Crear nuevo usuario"
            cbChangePassword.visibility = View.GONE
            lyPassword.visibility = View.VISIBLE
            etPassword.visibility = View.VISIBLE
            lyConfirmPassword.visibility = View.VISIBLE
            etConfirmPassword.visibility = View.VISIBLE
        }

        viewModel.loadUserTypes()

        cbChangePassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                lyPassword.visibility = View.VISIBLE
                etPassword.visibility = View.VISIBLE
                lyConfirmPassword.visibility = View.VISIBLE
                etConfirmPassword.visibility = View.VISIBLE
            } else {
                etPassword.text?.clear()
                etConfirmPassword.text?.clear()
                lyPassword.visibility = View.GONE
                etPassword.visibility = View.GONE
                lyConfirmPassword.visibility = View.GONE
                etConfirmPassword.visibility = View.GONE
            }
        }

        btnSave.setOnClickListener {
            validateAndSave()
        }
    }

    private fun setupUI() {
        // inicializar spinner adapter
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf<String>()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUserType.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        UserFormState.Loading -> showLoading(true)
                        is UserFormState.TypesLoaded -> {
                            showLoading(false)
                            userTypes = state.types
                            updateTypeSpinner()
                        }
                        is UserFormState.UserLoaded -> {
                            showLoading(false)
                            populateUserData(state.user)
                        }
                        UserFormState.CreateSuccess -> {
                            showLoading(false)
                            showSuccess("Usuario creado exitosamente")
                            navigateBackToList("Usuario creado exitosamente")
                        }
                        UserFormState.UpdateSuccess -> {
                            showLoading(false)
                            showSuccess("Usuario actualizado exitosamente")
                            navigateBackToList("Usuario actualizado exitosamente")
                        }
                        is UserFormState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun updateTypeSpinner() {
        val typeNames = userTypes.map { it.nombre }
        val adapter = spinnerUserType.adapter as ArrayAdapter<String>
        adapter.clear()
        adapter.addAll(typeNames)
        adapter.notifyDataSetChanged()
    }

    private fun populateUserData(user: UserComReponse) {
        etFirstName.setText(user.userInfo?.primer_nombre ?: "")
        etLastName.setText(user.userInfo?.apellido_pat ?: "")
        etUsername.setText(user.userInfo?.usuario ?: "")

        // establece el tipo usuario que corresponde
        val userTypeIndex = userTypes.indexOfFirst { it.id_tipo == user.tipo_usuario?.id_tipo }
        if (userTypeIndex != -1) {
            spinnerUserType.setSelection(userTypeIndex)
        }
    }

    private fun validateAndSave() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()) {
            showError("Complete todos los campos obligatorios")
            return
        }

        if (userId == 0) {
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                showError("La contraseña es obligatoria")
                return
            }

            if (password != confirmPassword) {
                showError("Las contraseñas no coinciden")
                return
            }

            val selectedType = userTypes[spinnerUserType.selectedItemPosition]
            viewModel.createUser(firstName, lastName, username, password, selectedType.id_tipo)
        } else {
            val passwordToUse = if (cbChangePassword.isChecked) {
                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    showError("Complete ambos campos de contraseña")
                    return
                }
                if (password != confirmPassword) {
                    showError("Las contraseñas no coinciden")
                    return
                }
                password
            } else {
                null
            }

            viewModel.updateUser(userId, firstName, lastName, username, passwordToUse)
        }
    }

    private fun navigateBackToList(message: String) {
        // Mostrar mensaje
        (requireActivity() as? MainActivity)?.showSnackbar(message)

        // Disparar evento de actualización
        parentFragmentManager.setFragmentResult("REFRESH_REQUEST", Bundle().apply {
            putBoolean("SHOULD_REFRESH", true)
        })

        // Volver
        parentFragmentManager.popBackStack()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
    }

    private fun showError(message: String) {
        // Usar la actividad como contexto y encontrar el root view
        val rootView = requireActivity().findViewById<View>(android.R.id.content)

        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.error))
            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            .setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    private fun showSuccess(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(requireContext(), R.color.success))
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                .show()
        }
    }
}