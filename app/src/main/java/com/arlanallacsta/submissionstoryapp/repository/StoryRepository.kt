package com.arlanallacsta.submissionstoryapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arlanallacsta.submissionstoryapp.api.ApiService
import com.arlanallacsta.submissionstoryapp.database.Story
import com.arlanallacsta.submissionstoryapp.database.StoryDatabase
import com.arlanallacsta.submissionstoryapp.remote.StoryRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(ExperimentalPagingApi::class)
class StoryRepository @Inject constructor(
    private val database: StoryDatabase,
    private val apiService: ApiService
) {

    fun getStories(auth: String) : Flow<PagingData<Story>> =
        Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = StoryRemoteMediator(
                database,
                apiService,
                generateAuthorization(auth)
            ),
            pagingSourceFactory ={
                database.storyDao().getStories()
            }
        ).flow

    private fun generateAuthorization(token: String): String {
        return "Bearer $token"
    }

}