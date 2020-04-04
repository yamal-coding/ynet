package com.yamal.ynet

sealed class ApiRequest<ResponseType> {
    abstract fun getPathSegments(): String

    abstract fun getResponseClass(): Class<ResponseType>

    open fun getQueryParams(): Map<String, String> = emptyMap()
}

abstract class ApiRequestGet<ResponseType> : ApiRequest<ResponseType>()

abstract class ApiRequestPost<RequestBodyType, ResponseType> : ApiRequest<ResponseType>() {
    abstract fun getBody(): RequestBodyType
}