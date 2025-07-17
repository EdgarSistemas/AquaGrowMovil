package com.example.aquagrow.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.R
import com.example.aquagrow.data.local.SessionManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword : TextInputEditText
    private lateinit var cbChangePassword : CheckBox
    private lateinit var btnSave: Button
    private lateinit var btnLogout: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var lyPassword : TextInputLayout
    private lateinit var lyConfirmPassword : TextInputLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etUsername = view.findViewById(R.id.etUsername)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        cbChangePassword = view.findViewById(R.id.cbChangePassword)
        btnSave = view.findViewById(R.id.btnSave)
        btnLogout = view.findViewById(R.id.btnLogout)
        progressBar = view.findViewById(R.id.progressBar)

        lyPassword = view.findViewById(R.id.lyPassword)
        lyConfirmPassword = view.findViewById(R.id.lyConfirmPassword)


        setupObservers()

        val userId = SessionManager.getUserId()
        if (userId != null) {
            viewModel.loadUserData(userId)
        }

        btnSave.setOnClickListener {
            val nombre = etFirstName.text.toString().trim()
            val apellido = etLastName.text.toString().trim()
            val usuario = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (nombre.isEmpty() || apellido.isEmpty() || usuario.isEmpty()) {
                showToast("Todos los campos son obligatorios")
                return@setOnClickListener
            }

            val finalPassword = if (cbChangePassword.isChecked) {
                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    showToast("Debes llenar ambos campos de contraseña")
                    return@setOnClickListener
                }
                if (password != confirmPassword) {
                    showToast("Las contraseñas no coinciden")
                    return@setOnClickListener
                }
                password
            } else {
                null
            }

            viewModel.updateUser(nombre, apellido, usuario, finalPassword)
        }

        btnLogout.setOnClickListener {
            com.example.aquagrow.data.remote.mqtt.MqttClientManager.disconnect()

            SessionManager.clearAuthData()
            com.example.aquagrow.AquagrowApp.shouldNavigateToMain = false

            val intent = Intent(requireActivity(), com.example.aquagrow.ui.auth.LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }

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

    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is ProfileState.Loading -> setLoading(true)
                        is ProfileState.Loaded -> {
                            setLoading(false)
                            etFirstName.setText(state.user.primer_nombre ?: "")
                            etLastName.setText(state.user.apellido_pat ?: "")
                            etUsername.setText(state.user.usuario ?: "")
                        }
                        is ProfileState.Success -> {
                            setLoading(false)
                            showToast("Perfil actualizado correctamente")
                        }
                        is ProfileState.Error -> {
                            setLoading(false)
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }

    private fun setLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSave.isEnabled = !show
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
