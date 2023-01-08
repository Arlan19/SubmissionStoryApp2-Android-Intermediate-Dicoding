package com.arlanallacsta.submissionstoryapp.login

import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.CoroutinesTestRule
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import junit.framework.Assert
import com.arlanallacsta.submissionstoryapp.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var loginViewModel: LoginViewModel

    private val dummyLoginResponse = DataDummy.generateDummyLoginResponse()
    private val dummyEmail = "dicoding@mail.com"
    private val dummyPassword = "123456"

    @Before
    fun setup() {
        loginViewModel = LoginViewModel(repository)
    }

    @Test
    fun `when Login is successfully return Result Success`(): Unit = runTest {
        val expectedResponse = flowOf(Result.Success(dummyLoginResponse))

        Mockito.`when`(loginViewModel.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.login(dummyEmail, dummyPassword).collect { result ->
            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(true)
                    Assert.assertNotNull(result.data)
                    Assert.assertSame(result.data, dummyLoginResponse)

                }
                is Result.Error -> {
                    Assert.assertFalse(result.data!!.error)
                }

                is Result.Loading ->{}
            }
        }
        Mockito.verify(repository).login(dummyEmail, dummyPassword)
    }

    @Test
    fun `when is Failed return Result Failed`(): Unit = runTest {
        val expectedResponse : Flow<Result<LoginResponse>> = flowOf(Result.Error("failed"))

        Mockito.`when`(loginViewModel.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        loginViewModel.login(dummyEmail, dummyPassword).collect { result ->
            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(false)
                    Assert.assertFalse(result.data!!.error)
                }
                is Result.Error -> {
                    Assert.assertNotNull(result.message)
                }

                is Result.Loading ->{}
            }
        }
        Mockito.verify(repository).login(dummyEmail, dummyPassword)
    }

}