package com.arlanallacsta.submissionstoryapp.register

import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.CoroutinesTestRule
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import com.arlanallacsta.submissionstoryapp.utils.Result
import junit.framework.Assert
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
class RegisterViewModelTest{

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var repository: Repository
    private lateinit var registerViewModel: RegisterViewModel

    private val dummyRegisterResponse = DataDummy.generateDummyRegisterResponse()
    private val dummyName = "code"
    private val dummyEmail = "Dicoding@mail.com"
    private val dummyPassword = "123456"

    @Before
    fun setup() {
        registerViewModel = RegisterViewModel(repository)
    }

    @Test
    fun `when Register is successfully return Result Success`(): Unit = runTest {
        val expectedResponse = flowOf(Result.Success(dummyRegisterResponse))

        Mockito.`when`(registerViewModel.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        registerViewModel.register(dummyName, dummyEmail, dummyPassword).collect { result ->
            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(true)
                    Assert.assertNotNull(result.data)
                    Assert.assertSame(result.data, dummyRegisterResponse)

                }
                is Result.Error -> {
                    Assert.assertFalse(result.data!!.error)
                }

                is Result.Loading ->{}
            }
        }
        Mockito.verify(repository).register(dummyName, dummyEmail, dummyPassword)
    }

    @Test
    fun `when Register is Failed return Result Failed`(): Unit = runTest {
        val expectedResponse : Flow<Result<RegisterResponse>> = flowOf(Result.Error("failed"))

        Mockito.`when`(registerViewModel.register(dummyName, dummyEmail, dummyPassword)).thenReturn(expectedResponse)

        registerViewModel.register(dummyName, dummyEmail, dummyPassword).collect { result ->
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
        Mockito.verify(repository).register(dummyName,dummyEmail, dummyPassword)
    }


}