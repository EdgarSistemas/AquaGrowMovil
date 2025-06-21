package com.example.aquagrow.data.remote.responses

import java.io.IOException

class ApiError(val code: Int, message: String) : IOException(message)