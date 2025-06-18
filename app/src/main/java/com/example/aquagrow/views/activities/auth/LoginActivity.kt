package com.example.aquagrow.views.activities.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aquagrow.R
import com.example.aquagrow.controller.AuthController

class LoginActivity : AppCompatActivity() {

    private lateinit var edtCorreo : EditText
    private lateinit var edtContrasenia : EditText
    private lateinit var btnIniciarSesion : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Inicializar variables
        val authController = AuthController(this)

        edtCorreo = findViewById(R.id.edtCorreo)
        edtContrasenia = findViewById(R.id.edtContrasenia)
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)

        btnIniciarSesion.setOnClickListener {
            val correo = edtCorreo.text.toString().strip()
            val contrasenia = edtContrasenia.text.toString().strip()

            authController.login(correo, contrasenia) { success, message ->
                if(success) {
                    //redirigir main
                } else {
                    // mostrar error
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRelativeLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}