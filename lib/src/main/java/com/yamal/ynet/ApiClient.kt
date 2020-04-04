package com.yamal.ynet

import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URI

class ApiClient(
    baseUrl: String,
    private val gson: Gson,
    private val headersInterceptor: HeadersInterceptor? = null,
    private val logger: ApiLogger? = null
) {
    private val mBaseUrl: HttpUrl
    private val okHttpClient = OkHttpClient.Builder().build()
    private var urlInterceptor: UrlInterceptor? = null

    init {
        val urlBuilder = HttpUrl.Builder()

        with(URI.create(baseUrl)) {
            host?.let { urlBuilder.host(it) }
            port.takeIf { it >= 0 }?.let { urlBuilder.port(it) }
            scheme?.let { urlBuilder.scheme(it) }
        }

        mBaseUrl = urlBuilder.build()
    }

    fun setUrlInterceptor(urlInterceptor: UrlInterceptor?) {
        this.urlInterceptor = urlInterceptor
    }

    fun <ResponseType> sendRequest(
        apiRequest: ApiRequest<ResponseType>,
        onSuccess: (ResponseType) -> Unit,
        onError: (ApiErrorResponse) -> Unit
    ) {
        val requestBuilder = Request.Builder().url(createUrl(apiRequest))

        requestBuilder
            .selectMethod(apiRequest)
            .addHeaders()

        try {
            val request = requestBuilder.build()
            logger?.logRequest(request.url.toString())
            val response = okHttpClient.newCall(request).execute()

            val body = response.body?.string()
            logger?.logResponse(body, response.code)

            if (response.isSuccessful) {
                val responseBody = gson.fromJson(
                    body,
                    apiRequest.getResponseClass()
                )

                responseBody?.let {
                    onSuccess(it)
                } ?: onError(MalformedApiResponse(Exception("Unexpected null response")))
            } else {
                val errorMessage = "API error response"
                logger?.logError(errorMessage)
                onError(GenericErrorResponse(Exception(errorMessage)))
            }
        } catch (e: IOException) {
            onError(ConnectivityErrorResponse(e))
        }
    }

    private fun  Request.Builder.selectMethod(apiRequest: ApiRequest<*>) = apply {
        when (apiRequest) {
            is ApiRequestGet -> get()
            is ApiRequestPost<*, *> -> {
                val requestBodyAsJson = gson.toJson(apiRequest.getBody())
                logger?.logRequest("Body $requestBodyAsJson")
                post(requestBodyAsJson.toRequestBody())
            }
        }
    }

    private fun Request.Builder.addHeaders() = apply {
        headersInterceptor?.getHeaders()?.forEach {
            addHeader(it.key, it.value)
        }
    }

    private fun createUrl(apiRequest: ApiRequest<*>): HttpUrl {
        val urlBuilder = mBaseUrl.newBuilder()

        apiRequest.getQueryParams().forEach {
            urlBuilder.addQueryParameter(it.key, it.value)
        }
        urlBuilder.addPathSegments(apiRequest.getPathSegments())

        urlInterceptor?.intercept(urlBuilder)

        return urlBuilder.build()
    }
}