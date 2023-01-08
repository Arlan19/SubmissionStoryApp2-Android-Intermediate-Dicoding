package com.arlanallacsta.submissionstoryapp.repository

import com.arlanallacsta.submissionstoryapp.utils.CoroutinesTestRule
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import junit.framework.Assert
import com.arlanallacsta.submissionstoryapp.utils.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RepositoryTest{
    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var dataSource: DataSource
    private lateinit var repository: Repository
    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    private val dummyName = "code"
    private val dummyEmail = "dicoding@mail.com"
    private val dummyPassword = "123456"

    @Before
    fun setup() {
        repository = Repository(dataSource)
    }

    @Test
    fun `when Login successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyLoginResponse()

        Mockito.`when`(dataSource.login(dummyEmail, dummyPassword)).thenReturn(Response.success(expectedResponse))

        repository.login(dummyEmail, dummyPassword).collect { result ->

            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(true)
                    Assert.assertNotNull(result.data)
                    assertEquals(expectedResponse, result.data)
                }
                is Result.Error -> {
                    Assert.assertFalse(result.data!!.error)
                    Assert.assertNull(result)
                }

                is Result.Loading ->{}
            }
        }
    }

    @Test
    fun `when Login Failed`(): Unit = runTest {
        Mockito.`when`(
            dataSource.login(
                dummyEmail,
                dummyPassword
            )
        ).then { throw Exception() }

        repository.login(dummyEmail, dummyPassword).collect { result ->
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
    }

    @Test
    fun `when Register successfully`(): Unit = runTest {
        val expectedResponse = DataDummy.generateDummyRegisterResponse()

        Mockito.`when`(dataSource.register(dummyName, dummyEmail, dummyPassword))
            .thenReturn(Response.success(expectedResponse))

        repository.register(dummyName, dummyEmail, dummyPassword).collect { result ->

            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(true)
                    Assert.assertNotNull(result.data)
                    assertEquals(expectedResponse, result.data)
                }
                is Result.Error -> {
                    Assert.assertFalse(result.data!!.error)
                    Assert.assertNull(result)
                }

                is Result.Loading ->{}
            }
        }
    }

    @Test
    fun `when Register failed`(): Unit = runTest {
        Mockito.`when`(
            dataSource.register(
                dummyName,
                dummyEmail,
                dummyPassword
            )
        ).then { throw Exception() }

        repository.register(dummyName, dummyEmail, dummyPassword).collect { result ->
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
    }

    @Test
    fun `when Upload image file return successfully`() = runTest {
        val expectedResponse = DataDummy.generateDummyFileUploadResponse()

        Mockito.`when`(
            dataSource.uploadStory(
                "Bearer $dummyToken",
                "text",
                "",
                "",
                dummyMultipart
            )
        ).thenReturn(Response.success(expectedResponse))

        repository.uploadStory(dummyToken, "text", "", "",dummyMultipart)
            .collect { result ->
                when(result){
                    is Result.Success -> {
                        Assert.assertNotNull(result.data)
                        Assert.assertTrue(true)
                    }
                    is Result.Error -> {}
                    is Result.Loading ->{}
                }
            }

        Mockito.verify(dataSource)
            .uploadStory(
                "Bearer $dummyToken",
                "text",
                "",
                "",
                dummyMultipart
            )
    }

    @Test
    fun `when Upload image file throw exception`() = runTest {

        Mockito.`when`(
            dataSource.uploadStory(
                "Bearer $dummyToken",
                dummyDescription,
                "",
                "",
                dummyMultipart
            )
        ).then { throw Exception() }

        repository.uploadStory(dummyToken, dummyDescription, "", "",dummyMultipart)
            .collect { result ->
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

        Mockito.verify(dataSource).uploadStory(
            "Bearer $dummyToken",
            dummyDescription,
            "",
            "",
            dummyMultipart
        )
    }
}