package com.arlanallacsta.submissionstoryapp.api

import com.arlanallacsta.submissionstoryapp.utils.Result
import org.json.JSONObject
import retrofit2.Response


@Suppress("BlockingMethodInNonBlockingContext")
abstract class ApiConfig {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Result<T> {
        try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return Result.Success(body)
                }
            }
            return error(getErrorMessage(message = response.errorBody()!!.string()))
        } catch (e: Exception) {
            return error("${e.message} : $e")
        }
    }

    private fun getErrorMessage(message: String): String {
        val obj = JSONObject(message)
        return obj.getString("message")
    }

    private fun <T> error(errorMessage: String, data: T? = null): Result<T> =
        Result.Error(errorMessage, data)
}