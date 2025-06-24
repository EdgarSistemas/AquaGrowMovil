package com.example.aquagrow.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.aquagrow.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout usando inflater
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Acceder a vistas con findViewById (nota: es necesario llamar a findViewById en la vista raíz)
        val textView = view.findViewById<TextView>(R.id.tvTitle)
        textView.text = "Panel de Control"

        return view
    }
}