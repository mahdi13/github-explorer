package com.perfect.githubexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.perfect.githubexplorer.data.*
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Deferred
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.*
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior

import java.util.concurrent.TimeUnit

class ViewModelTest {

    @get:Rule
    val instantTaskRule = InstantTaskExecutorRule()

    @RelaxedMockK
    lateinit var loadingStateObserver: Observer<LoadingStatus>

    @RelaxedMockK
    lateinit var repositoriesObserver: Observer<PagedList<Repository>>

    lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = SearchViewModel()

        val behavior = NetworkBehavior.create()
        behavior.setDelay(100.toLong(), TimeUnit.MILLISECONDS)
        val mockRetrofit = MockRetrofit.Builder(apiRetrofit)
            .networkBehavior(behavior)
            .build()

        val delegate = mockRetrofit.create(GithubApiInterface::class.java)
        val mockApiClient = MockGitHub(delegate)

        mockkObject(lazyApiClient)
        every { lazyApiClient.value } returns mockApiClient

    }

    @After
    fun after() {
        unmockkAll()
    }

    class MockGitHub(private val delegate: BehaviorDelegate<GithubApiInterface>) : GithubApiInterface {
        override fun searchRepositories(query: String, page: Int, pageSize: Int): Deferred<SearchRepositoryResponse> =
            delegate.returningResponse(
                SearchRepositoryResponse(
                    mutableListOf<Repository>().apply { repeat(pageSize) { add(Repository(it)) } }, pageSize, false
                )
            ).searchRepositories(query, page, page)

        override fun userProfile(username: String): Deferred<User> = throw NotImplementedError()

        override fun userRepositories(username: String, page: Int, pageSize: Int): Deferred<List<Repository>> =
            throw NotImplementedError()

        override fun repositoryDetail(id: Int): Deferred<Repository> = throw NotImplementedError()
    }

    @Test
    fun testSearchViewModel() {

        viewModel.loadingState.observeForever(loadingStateObserver)
        viewModel.repositories.observeForever(repositoriesObserver)
        viewModel.query.value = "mockq"

        // Loading the first 3 pages
        Thread.sleep(200)
        verify {
            repositoriesObserver.onChanged(match {
                it.loadedCount == DEFAULT_PAGE_SIZE * 3
            })
        }

        // Try to load the last loaded item
        viewModel.repositories.value!!.loadAround(DEFAULT_PAGE_SIZE * 3 - 1)
        Thread.sleep(200)

        // Load 4th page
        verify {
            repositoriesObserver.onChanged(match {
                it.loadedCount == DEFAULT_PAGE_SIZE * 4
            })
        }

        verifyOrder {
            // First 3 pages
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            loadingStateObserver.onChanged(LoadingStatus.LOADED)

            // 4th page
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            loadingStateObserver.onChanged(LoadingStatus.LOADED)
        }

    }
}
