package com.myapp.nowinandroid

import com.myapp.nowinandroid.data.NiARepository
import com.myapp.nowinandroid.ui.NiAViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NiAViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var viewModel: NiAViewModel
    private lateinit var repository: NiARepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = NiARepository()
        viewModel = NiAViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun toggleFollowTopic_updatesTopicState() = runTest {
        val topicId = "1"
        val initialFollowed = viewModel.topics.value.find { it.id == topicId }?.isFollowed ?: false
        
        viewModel.toggleFollowTopic(topicId)
        
        val updatedFollowed = viewModel.topics.value.find { it.id == topicId }?.isFollowed
        assertEquals(!initialFollowed, updatedFollowed)
    }

    @Test
    fun toggleSaveNews_updatesNewsState() = runTest {
        val newsId = "1"
        val initialSaved = viewModel.newsResources.value.find { it.id == newsId }?.isSaved ?: false
        
        viewModel.toggleSaveNews(newsId)
        
        val updatedSaved = viewModel.newsResources.value.find { it.id == newsId }?.isSaved
        assertEquals(!initialSaved, updatedSaved)
    }

    @Test
    fun followedTopics_onlyContainsFollowedTopics() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.followedTopics.collect {}
        }

        viewModel.toggleFollowTopic("1")
        viewModel.toggleFollowTopic("2")
        
        val followed = viewModel.followedTopics.value
        assertEquals(2, followed.size)
        assertTrue(followed.all { it.isFollowed })
        
        collectJob.cancel()
    }
}
