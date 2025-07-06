package com.example.aquagrow.data.model.responses

import com.example.aquagrow.data.model.domain.Unit
import com.google.gson.annotations.SerializedName

// informacion basica de la unidad
// usada en endpoint:
// /unit/get_units_basic
// /unit/get_units_with_user
data class UnitResponse(
    val units: List<Unit>
)