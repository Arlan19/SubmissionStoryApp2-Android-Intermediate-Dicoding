package com.arlanallacsta.submissionstoryapp.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.arlanallacsta.submissionstoryapp.database.Story
import com.arlanallacsta.submissionstoryapp.story.StoryAdapter
import com.arlanallacsta.submissionstoryapp.utils.CoroutinesTestRule
import com.arlanallacsta.submissionstoryapp.utils.DataDummy
import com.arlanallacsta.submissionstoryapp.utils.PagedTestDataSource
import com.arlanallacsta.submissionstoryapp.utils.getOrAwaitValue
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()

    @Mock
    private lateinit var viewModel : MainViewModel

    private val dummyToken = DataDummy.generateDummyToken()

    @Test
    fun `when open Main Get stories success`() = runTest {
        val dummyStories = DataDummy.generateDummyListStory()
        val data = PagedTestDataSource.snapshot(dummyStories)

        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        Mockito.`when`(viewModel.getStories(dummyToken)).thenReturn(stories)

        val actualStories = viewModel.getStories(dummyToken).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DiffCallback,
            updateCallback = noopListUpdateCallback,
            mainDispatcher = coroutinesTestRule.testDispatcher,
            workerDispatcher = coroutinesTestRule.testDispatcher
        )
        differ.submitData(actualStories)

        advanceUntilIdle()

        Mockito.verify(viewModel).getStories(dummyToken)
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
    }


    private val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

}