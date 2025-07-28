package com.example.aquagrow.util

import com.example.aquagrow.data.model.domain.Unit

object UnidadCache {
    private val unidadIdToNombreMap = mutableMapOf<Int, String>()

    fun setUnidades(unidades: List<Unit>) {
        unidadIdToNombreMap.clear()
        for (unidad in unidades) {
            unidadIdToNombreMap[unidad.id_unidad] = unidad.nombre
        }
    }

    fun getNombre(unidadId: Int): String {
        return unidadIdToNombreMap[unidadId] ?: "Unidad $unidadId"
    }
}