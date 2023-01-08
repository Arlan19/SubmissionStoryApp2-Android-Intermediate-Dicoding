package com.arlanallacsta.submissionstoryapp.repository

import com.arlanallacsta.submissionstoryapp.api.ApiConfig
import com.arlanallacsta.submissionstoryapp.login.LoginResponse
import com.arlanallacsta.submissionstoryapp.main.MainResponse
import com.arlanallacsta.submissionstoryapp.register.RegisterResponse
import com.arlanallacsta.submissionstoryapp.story.StoryResponse
import com.arlanallacsta.submissionstoryapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val dataSource: DataSource
) : ApiConfig() {

    suspend fun login(email: String, password: String): Flow<Result<LoginResponse>> =
        flow {
            emit(safeApiCall {
                dataSource.login(email, password)
            })
        }.flowOn(Dispatchers.IO)

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Flow<Result<RegisterResponse>> = flow {
        emit(safeApiCall {
            dataSource.register(name, email, password)
        })
    }.flowOn(Dispatchers.IO)

    suspend fun getStoriesLocation(auth: String): Flow<Result<MainResponse>> =
        flow {
            emit(safeApiCall {
                dataSource.getStoriesLocation(generateAuthorization(auth))
            })
        }.flowOn(Dispatchers.IO)

    suspend fun uploadStory(
        auth: String,
        description: String,
        lat: String?,
        lon: String?,
        file: MultipartBody.Part
    ): Flow<Result<StoryResponse>> =
        flow {
            emit(safeApiCall {
                val generateToken = generateAuthorization(auth)
                dataSource.uploadStory(generateToken, description, lat, lon, file)
            })
        }.flowOn(Dispatchers.IO)

    private fun generateAuthorization(token: String): String {
        return "Bearer $token"
    }

}