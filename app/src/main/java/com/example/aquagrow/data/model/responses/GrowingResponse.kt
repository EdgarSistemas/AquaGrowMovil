package com.example.aquagrow.data.model.responses

import com.example.aquagrow.data.model.domain.Growing

data class GrowingIdUnitResponse (
    val growing : Growing
)

data class GrowingsIdUnitResponse (
    val growings : List<Growing>
)

data class GrowingAddUpdResponse (
    val id_cultivo : Int,
    val mensaje : String
)