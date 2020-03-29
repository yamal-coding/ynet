package com.yamal.ynet

interface ApiLogger {
    fun logRequest(url: String)

    fun logResponse(body: String?, code: Int)

    fun logError(errorMessage: String)
}
