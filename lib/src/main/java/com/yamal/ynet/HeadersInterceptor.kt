package com.yamal.ynet

interface HeadersInterceptor {
    fun getHeaders(): Map<String, String>
}