package com.yamal.ynet

import android.util.Log

class BasicApiLogger(private val logTag: String) : ApiLogger {
	override fun logRequest(url: String) {
        Log.d(logTag, "Request $url")
    }

    override fun logResponse(body: String?, code: Int) {
        Log.d(logTag, "Response $code $body")
    }

    override fun logError(errorMessage: String) {
        Log.d(logTag, "Api error: $errorMessage")
    }
}