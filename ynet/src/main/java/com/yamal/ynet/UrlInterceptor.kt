package com.yamal.ynet

import okhttp3.HttpUrl

interface UrlInterceptor {
    fun intercept(urlBuilder: HttpUrl.Builder)
}