package com.example.aquagrow.ui.growing

import com.example.aquagrow.data.model.domain.Growing

sealed class GrowingFormState {
    object Loading : GrowingFormState()
    data class Error(val message: String) : GrowingFormState()
    data class Form(val growing: Growing?) : GrowingFormState()
    object Success : GrowingFormState()
}
