package com.arlanallacsta.submissionstoryapp.repository

import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.ListUpdateCallback
import com.arlanallacsta.submissionstoryapp.api.ApiService
import com.arlanallacsta.submissionstoryapp.database.StoryDatabase
import com.arlanallacsta.submissionstoryapp.story.StoryAdapter
import com.arlanallacsta.submissionstoryapp.utils.CoroutinesTestRule
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import com.arlanallacsta.submissionstoryapp.utils.PagedTestDataSource
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryTest{

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var storyDatabase: StoryDatabase

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var storyRepositoryMock: StoryRepository

    private lateinit var storyRepository: StoryRepository

    private val dummyToken = DataDummy.generateDummyToken()
    private val dummyStoriesResponse = DataDummy.generateDummyStoriesResponse()

    @Before
    fun setup() {
        storyRepository = StoryRepository(storyDatabase, apiService)
    }

    @Test
    fun `when Get stories with pager return successfully`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val expectedResult = flowOf(data)

        Mockito.`when`(storyRepositoryMock.getStories(dummyToken)).thenReturn(expectedResult)

        storyRepositoryMock.getStories(dummyToken).collect { result ->
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DiffCallback,
                updateCallback = noopListUpdateCallback,
                mainDispatcher = coroutinesTestRule.testDispatcher,
                workerDispatcher = coroutinesTestRule.testDispatcher
            )
            differ.submitData(result)
            Assert.assertNotNull(differ.snapshot())
            assertEquals(
                dummyStoriesResponse.listStory.size,
                differ.snapshot().size
            )
        }

    }

    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}