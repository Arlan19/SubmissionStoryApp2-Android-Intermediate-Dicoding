package com.arlanallacsta.submissionstoryapp.maps

import androidx.paging.ExperimentalPagingApi
import com.arlanallacsta.submissionstoryapp.main.MainResponse
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
import com.arlanallacsta.submissionstoryapp.utils.Result
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest {

    @Mock
    private lateinit var repository: Repository
    private lateinit var mapsViewModel: MapsViewModel

    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()
    private val dummyToken = DataDummy.generateDummyToken()

    @Before
    fun setup() {
        mapsViewModel = MapsViewModel(repository)
    }

    @Test
    fun `when Get location story is successfully return NetworkResult Success`(): Unit = runTest {
        val expectedResponse = flowOf(Result.Success(dummyStoriesResponse))

        Mockito.`when`(mapsViewModel.getStoriesLocation(dummyToken)).thenReturn(expectedResponse)

        mapsViewModel.getStoriesLocation(dummyToken).collect { result ->
            when(result) {
                is Result.Success -> {
                    Assert.assertTrue(true)
                    Assert.assertNotNull(result.data)
                    Assert.assertSame(result.data, dummyStoriesResponse)

                }
                is Result.Error -> {
                    Assert.assertFalse(result.data!!.error)
                }

                is Result.Loading ->{}
            }
        }

        Mockito.verify(repository).getStoriesLocation(dummyToken)
    }

    @Test
    fun `when Get location story is failed rerturn NetworkResult Error`(): Unit = runTest {
        val expectedResponse : Flow<Result<MainResponse>> = flowOf(Result.Error("failed"))

        Mockito.`when`(mapsViewModel.getStoriesLocation(dummyToken)).thenReturn(expectedResponse)

        mapsViewModel.getStoriesLocation(dummyToken).collect { result ->
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

        Mockito.verify(repository).getStoriesLocation(dummyToken)
    }


}