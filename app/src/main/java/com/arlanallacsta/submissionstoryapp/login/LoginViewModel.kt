package com.arlanallacsta.submissionstoryapp.login

import androidx.lifecycle.ViewModel
import com.arlanallacsta.submissionstoryapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    suspend fun login(email: String, password: String) = repository.login(email, password)

}