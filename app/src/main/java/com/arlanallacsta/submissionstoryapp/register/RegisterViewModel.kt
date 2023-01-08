package com.arlanallacsta.submissionstoryapp.register

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun register(name: String, email: String, password: String) = repository.register(name, email, password)
}