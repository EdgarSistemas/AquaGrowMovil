package com.example.aquagrow.data.remote.responses

import okhttp3.Response
import java.io.IOException

class ApiError(
    val response: Response?,
    val errorBody: String?,
    message: String
) : IOException(message)