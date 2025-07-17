package com.example.aquagrow.ui.zone

import com.example.aquagrow.data.model.responses.ConfigZoneResponse
import com.example.aquagrow.data.model.responses.IrrigationByIdResponse

sealed class ZoneConfigState {
    object Loading : ZoneConfigState()
    data class Loaded(
        val configZone: ConfigZoneResponse?,
        val programacionRiego: IrrigationByIdResponse?,
        val isExistingConfig: Boolean,
        val isExistingSchedule: Boolean
    ) : ZoneConfigState()
    data class Success(val mensaje: String) : ZoneConfigState()
    data class Error(val mensaje: String) : ZoneConfigState()
}
