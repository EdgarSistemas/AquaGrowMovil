package com.example.aquagrow.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.aquagrow.AquagrowApp
import com.example.aquagrow.R
import com.example.aquagrow.data.remote.mqtt.MqttClientManager
import com.example.aquagrow.data.remote.mqtt.MqttInitializer
import com.example.aquagrow.ui.main.MainActivity
import com.example.aquagrow.ui.notifications.MqttNotificationHandler
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var btnIniciarSesion: Button
    private lateinit var edtCorreo: EditText
    private lateinit var edtContrasenia: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var mainLayout: RelativeLayout

    private val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si venimos de una sesión expirada
        if (intent.getBooleanExtra("SESSION_EXPIRED", false)) {
            showError("Tu sesión ha expirado. Por favor inicia sesión nuevamente.")
        }

        // Verificar inicialización de AquagrowApp
        if (!AquagrowApp.isInitialized) {
            showFatalError("Error crítico de configuración")
            return
        }

        if (AquagrowApp.shouldNavigateToMain) {
            Log.d("LoginActivity", "Usuario ya autenticado, saltando a MainActivity")
            startMainActivity()
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicializar vistas
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        edtCorreo = findViewById(R.id.edtCorreo)
        edtContrasenia = findViewById(R.id.edtContrasenia)
        progressBar = findViewById(R.id.progressBar)
        mainLayout = findViewById(R.id.mainRelativeLayout)

        setupObservers()
        initViews()

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupObservers() {
        Log.d("LoginActivity", "Configurando observadores...")
        lifecycleScope.launch {
            Log.d("LoginActivity", "Dentro de lifecycleScope")

            // Usa repeatOnLifecycle para manejar correctamente el ciclo de vida
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Log.d("LoginActivity", "Dentro de repeatOnLifecycle")

                viewModel.loginState.collect { state ->
                    Log.d("LoginActivity", "Estado recibido: $state")

                    when (state) {
                        LoginState.Loading -> {
                            Log.d("LoginActivity", "Mostrando carga...")
                            btnIniciarSesion.isEnabled = false
                            progressBar.visibility = View.VISIBLE
                        }
                        is LoginState.Success -> {
                            Log.d("LoginActivity", "Login exitoso, navegando...")
                            progressBar.visibility = View.GONE
                            MqttInitializer.initAfterLogin()
                            startMainActivity()
                        }
                        is LoginState.Error -> {
                            Log.e("LoginActivity", "Error: ${state.message}")
                            btnIniciarSesion.isEnabled = true
                            progressBar.visibility = View.GONE
                            showError("Usuario y/o contraseña incorrectos: " + state.message)
                        }
                        LoginState.Idle -> {
                            Log.d("LoginActivity", "Estado inactivo")
                            btnIniciarSesion.isEnabled = true
                            progressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun initViews() {
        btnIniciarSesion.setOnClickListener {
            val username = edtCorreo.text.toString().trim()
            val password = edtContrasenia.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showError("Usuario y contraseña son requeridos")
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    private fun startMainActivity() {
        runCatching {
            Log.d("LoginActivity", "Intentando iniciar MainActivity")

            // Verifica si la actividad existe
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            // Comprueba si hay un receptor para la actividad
            if (intent.resolveActivity(packageManager) != null) {
                Log.d("LoginActivity", "MainActivity encontrada, iniciando...")
                startActivity(intent)
                Log.d("LoginActivity", "MainActivity iniciada")
            } else {
                Log.e("LoginActivity", "MainActivity no encontrada en el manifest")
                Toast.makeText(this, "Error: Actividad principal no disponible", Toast.LENGTH_LONG).show()
            }
        }.onFailure { e ->
            Log.e("LoginActivity", "Error al iniciar MainActivity", e)
            Toast.makeText(this, "Error crítico: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showLoading(show: Boolean) {
        btnIniciarSesion.isEnabled = !show
        if (show) {
            Toast.makeText(this, "Cargando...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showFatalError(message: String) {
        android.app.AlertDialog.Builder(this)
            .setTitle("Error Crítico")
            .setMessage(message)
            .setPositiveButton("Salir") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}