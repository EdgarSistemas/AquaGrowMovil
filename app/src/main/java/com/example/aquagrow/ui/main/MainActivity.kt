package com.example.aquagrow.ui.main

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aquagrow.R
import com.example.aquagrow.data.local.SessionManager
import com.example.aquagrow.data.model.domain.Permission
import com.example.aquagrow.ui.dashboard.DashboardFragment
import com.example.aquagrow.ui.main.dashboard.UserFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        fragmentContainer = findViewById(R.id.fragment_container)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Configurar Bottom Navigation dinámicamente
        setupBottomNavigation()

        // Cargar fragmento inicial
        if (savedInstanceState == null) {
            loadInitialFragment()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupBottomNavigation() {
        val permissions = SessionManager.getPermissions()

        // Crear menú dinámico basado en permisos
        val menu = bottomNavigation.menu
        menu.clear()

        permissions.forEachIndexed { index, permission ->
            val menuItem = menu.add(
                Menu.NONE,
                index,
                Menu.NONE,
                permission.nombre_modulo
            )

            // Asignar icono basado en el nombre
            menuItem.icon = getIconForModule(permission.icono)
        }

        // Configurar listener para cambio de ítems
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            val position = item.itemId
            if (position < permissions.size) {
                loadFragmentForPermission(permissions[position])
                true
            } else {
                false
            }
        }
    }

    private fun getIconForModule(iconName: String): Drawable? {
        // Mapear nombres de iconos a recursos drawable
        val iconResource = when (iconName.toLowerCase()) {
            "grafica" -> R.drawable.ic_dashboard
            "usuarios" -> R.drawable.ic_users
            // agregar mas icocno para los demas modulos
            "plantas" -> R.drawable.ic_plant
            else -> R.drawable.ic_fish
        }

        return ContextCompat.getDrawable(this, iconResource)
    }

    private fun loadInitialFragment() {
        val permissions = SessionManager.getPermissions()
        if (permissions.isNotEmpty()) {
            loadFragmentForPermission(permissions[0])
        } else {
            // Cargar fragmento por defecto si no hay permisos
            loadFragment(DashboardFragment(), "Dashboard")
        }
    }

    private fun loadFragmentForPermission(permission: Permission) {
        val fragment = when (permission.nombre_modulo) {
            "Dashboard" -> DashboardFragment()
            "Usuarios" -> UserFragment()
            // agregar mas fragments para os demas modulos
            else -> DashboardFragment()
        }

        loadFragment(fragment, permission.nombre_modulo)
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_nav_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            logoutUser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        SessionManager.clearAuthData()
        com.example.aquagrow.AquagrowApp.shouldNavigateToMain = false

        startActivity(Intent(this, com.example.aquagrow.ui.auth.LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}