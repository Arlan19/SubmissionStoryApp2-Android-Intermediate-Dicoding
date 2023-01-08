package com.arlanallacsta.submissionstoryapp.story

import androidx.paging.ExperimentalPagingApi
import com.arlanallacsta.submissionstoryapp.repository.Repository
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import com.arlanallacsta.submissionstoryapp.utils.Result
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryActivityTest{

    @Mock
    private lateinit var repository: Repository

    @Mock
    private lateinit var storyViewModel: StoryViewModel

    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyUploadResponse = DataDummy.generateDummyFileUploadResponse()
    private val dummyMultipart = DataDummy.generateDummyMultipartFile()
    private val dummyDescription = DataDummy.generateDummyRequestBody()

    @Before
    fun setUp() {
        storyViewModel = StoryViewModel(repository)
    }

    @Test
    fun `Upload file successfully`() = runTest {
        val expectedResponse = flowOf(Result.Success(dummyUploadResponse))

        Mockito.`when`(
            storyViewModel.uploadStory(
                dummyToken,
                dummyDescription,
                "",
                "",
                dummyMultipart
            )
        ).thenReturn(expectedResponse)

        repository.uploadStory(dummyToken, dummyDescription, "", "", dummyMultipart)
            .collect { result ->
                when(result){
                    is Result.Success -> {
                        Assert.assertNotNull(result.data)
                        Assert.assertTrue(true)
                        Assert.assertSame(dummyUploadResponse, result.data)
                    }
                    is Result.Error -> Assert.assertFalse(result.data!!.error)
                    is Result.Loading ->{}
                }
            }

        Mockito.verify(repository)
            .uploadStory(dummyToken, dummyDescription, "", "", dummyMultipart)
    }

    @Test
    fun `Upload file Failed`() = runTest {
        val expectedResponse : Flow<Result<StoryResponse>> = flowOf(Result.Error("failed"))

        Mockito.`when`(
            storyViewModel.uploadStory(
                dummyToken,
                dummyDescription,
                "",
                "",
                dummyMultipart
            )
        ).thenReturn(expectedResponse)

        repository.uploadStory(dummyToken, dummyDescription, "", "", dummyMultipart)
            .collect { result ->
                when(result){
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

        Mockito.verify(repository)
            .uploadStory(dummyToken, dummyDescription, "", "", dummyMultipart)
    }
}