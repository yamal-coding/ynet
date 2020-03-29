package com.yamal.ynet

sealed class ApiErrorResponse

data class MalformedApiResponse(val exception: Exception) : ApiErrorResponse()

data class ConnectivityErrorResponse(val exception: Exception) : ApiErrorResponse()

data class GenericErrorResponse(val exception: Exception): ApiErrorResponse()