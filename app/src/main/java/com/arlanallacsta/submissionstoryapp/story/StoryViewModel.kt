package com.arlanallacsta.submissionstoryapp.story

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun uploadStory(auth: String, description: String, lat: String?, lon: String?, file: MultipartBody.Part) =
        repository.uploadStory(auth, description, lat, lon, file)


}