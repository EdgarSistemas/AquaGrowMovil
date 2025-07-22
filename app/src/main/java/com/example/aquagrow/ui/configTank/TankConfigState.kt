package com.example.aquagrow.ui.configTank

import com.example.aquagrow.data.model.responses.ConfigTankResponse
import com.example.aquagrow.data.model.responses.FeedingScheduleResponse

sealed class TankConfigState {
    object Loading : TankConfigState()
    data class Loaded(
        val configTank: ConfigTankResponse?,
        val feedingSchedule: FeedingScheduleResponse?,
        val isExistingConfig: Boolean,
        val isExistingSchedule: Boolean
    ) : TankConfigState()
    data class Success(val mensaje: String) : TankConfigState()
    data class Error(val mensaje: String) : TankConfigState()
}