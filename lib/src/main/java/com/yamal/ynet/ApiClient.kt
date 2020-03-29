package com.yamal.ynet

import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.net.URI

class ApiClient(
    baseUrl: String,
    private val gson: Gson,
    private val logger: ApiLogger?
) {
    private val mBaseUrl = URI.create(baseUrl)
    private val okHttpClient = OkHttpClient.Builder().build()
    private var urlInterceptor: UrlInterceptor? = null

    fun setUrlInterceptor(urlInterceptor: UrlInterceptor?) {
        this.urlInterceptor = urlInterceptor
    }

    fun <ResponseType> sendRequest(
        apiRequest: ApiRequest<ResponseType>,
        onSuccess: (ResponseType) -> Unit,
        onError: (ApiErrorResponse) -> Unit
    ) {
        val requestBuilder = Request.Builder().url(createUrl(apiRequest))

        when (apiRequest.getMethod()) {
            HttpMethod.GET -> requestBuilder.get()
        }

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

    private fun createUrl(apiRequest: ApiRequest<*>): HttpUrl {
        val urlBuilder = HttpUrl.Builder()

        mBaseUrl.host?.let { urlBuilder.host(it) }
        mBaseUrl.port.takeIf { it >= 0 }?.let { urlBuilder.port(it) }
        mBaseUrl.scheme?.let { urlBuilder.scheme(it) }
        apiRequest.getQueryParams().forEach {
            urlBuilder.addQueryParameter(it.key, it.value)
        }
        urlBuilder.addPathSegments(apiRequest.getPathSegments())

        urlInterceptor?.intercept(urlBuilder)

        return urlBuilder.build()
    }
}