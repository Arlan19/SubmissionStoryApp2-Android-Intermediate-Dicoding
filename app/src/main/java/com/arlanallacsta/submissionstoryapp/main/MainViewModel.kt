package com.arlanallacsta.submissionstoryapp.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arlanallacsta.submissionstoryapp.database.Story
import com.arlanallacsta.submissionstoryapp.repository.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val storyRepository: StoryRepository): ViewModel() {

    fun getStories(auth: String) : LiveData<PagingData<Story>> =
        storyRepository.getStories(auth).cachedIn(viewModelScope).asLiveData()
}