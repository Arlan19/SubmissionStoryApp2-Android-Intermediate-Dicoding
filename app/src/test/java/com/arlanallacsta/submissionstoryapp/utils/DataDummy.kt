package com.arlanallacsta.submissionstoryapp.utils

import com.arlanallacsta.submissionstoryapp.database.Story
import com.arlanallacsta.submissionstoryapp.login.LoginResponse
import com.arlanallacsta.submissionstoryapp.login.LoginResult
import com.arlanallacsta.submissionstoryapp.main.ListStory
import com.arlanallacsta.submissionstoryapp.main.MainResponse
import com.arlanallacsta.submissionstoryapp.register.RegisterResponse
import com.arlanallacsta.submissionstoryapp.story.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object DataDummy {
    fun generateDummyStoriesResponse(): MainResponse {
        val error = false
        val message = "Stories fetched successfully"
        val listStory = mutableListOf<ListStory>()

        for (i in 0 until 10) {
            val story = ListStory(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                longitude = -16.002,
                latitude = -10.212
            )

            listStory.add(story)
        }

        return MainResponse(error, message, listStory)
    }

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 until 10) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )

            items.add(story)
        }

        return items
    }


    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            userId = "story-DyGewy241D6ZmJI9",
            name = "cod",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXNHamQzZWx0Wk1zckl1M3IiLCJpYXQiOjE2NTcyMTc2NjV9.ZlZaTNeZX3Db4KYwTkIaiUTBy5oX-3DkSmlSnpSuZws"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyMultipartFile(): MultipartBody.Part {
        val dummyText = "text"
        return MultipartBody.Part.create(dummyText.toRequestBody())
    }

    fun generateDummyRequestBody(): String {
        return "text"
    }

    fun generateDummyFileUploadResponse(): StoryResponse {
        return StoryResponse(
            error = false,
            message = "success"
        )
    }

    fun generateDummyToken() : String {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXNHamQzZWx0Wk1zckl1M3IiLCJpYXQiOjE2NTcyMTc2NjV9.ZlZaTNeZX3Db4KYwTkIaiUTBy5oX-3DkSmlSnpSuZws"
    }
}