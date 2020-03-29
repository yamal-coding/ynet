package com.yamal.ynet

interface ApiRequest<ResponseType> {

    fun getMethod(): HttpMethod

    fun getPathSegments(): String

    fun getResponseClass(): Class<ResponseType>

    fun getQueryParams(): Map<String, String> = emptyMap()
}

enum class HttpMethod {
    GET
}