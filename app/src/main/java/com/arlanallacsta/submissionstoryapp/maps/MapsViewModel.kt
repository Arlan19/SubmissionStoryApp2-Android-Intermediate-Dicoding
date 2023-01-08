package com.arlanallacsta.submissionstoryapp.maps

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapsViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    suspend fun getStoriesLocation(auth: String) = repository.getStoriesLocation(auth)
}