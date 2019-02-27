package com.perfect.githubexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.perfect.githubexplorer.data.*
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Deferred
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

    @RelaxedMockK
    lateinit var userObserver: Observer<User?>

    @RelaxedMockK
    lateinit var repositoryObserver: Observer<Repository?>

    @Before
    fun setup() {
        MockKAnnotations.init(this)

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

        override fun userProfile(username: String): Deferred<User> = delegate.returningResponse(
            User(123456, "mock-username")
        ).userProfile(username)

        override fun userRepositories(username: String, page: Int, pageSize: Int): Deferred<List<Repository>> =
            delegate.returningResponse(
                mutableListOf<Repository>().apply {
                    repeat(pageSize) {
                        add(Repository(it, owner = User(123456, "mock-username")))
                    }
                }
            ).userRepositories(username, page, pageSize)

        override fun repositoryDetail(id: Int): Deferred<Repository> = delegate.returningResponse(
            Repository(54321, owner = User(123456, "mock-username"))
        ).repositoryDetail(id)

    }

    @Test
    fun testSearchViewModel() {
        val searchViewModel = SearchViewModel()

        searchViewModel.loadingStatus.observeForever(loadingStateObserver)
        searchViewModel.repositories.observeForever(repositoriesObserver)
        searchViewModel.query.value = "mockq"

        // Loading the first 3 pages
        Thread.sleep(200)
        verify {
            repositoriesObserver.onChanged(match {
                it.loadedCount == DEFAULT_PAGE_SIZE * 3
            })
        }

        // Try to load the last loaded item
        searchViewModel.repositories.value!!.loadAround(DEFAULT_PAGE_SIZE * 3 - 1)
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

    @Test
    fun testUserProfileViewModel() {
        val profileViewModel = ProfileViewModel()

        profileViewModel.loadingStatus.observeForever(loadingStateObserver)
        profileViewModel.repositories.observeForever(repositoriesObserver)
        profileViewModel.user.observeForever(userObserver)

        profileViewModel.username.value = "mock-username"

        Thread.sleep(500)

        verify {
            userObserver.onChanged(match { it.username == "mock-username" })
        }
        verifyOrder {
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            // First 3 pages
            repositoriesObserver.onChanged(match { it.loadedCount == 60 })
            loadingStateObserver.onChanged(LoadingStatus.LOADED)
        }

    }

    @Test
    fun testRepositoryViewModel() {
        val repositoryViewModel = RepositoryViewModel()

        repositoryViewModel.repository.observeForever(repositoryObserver)

        repositoryViewModel.repositoryId.value = 54321

        Thread.sleep(500)

        verify {
            repositoryObserver.onChanged(match { it.id == 54321 })
        }

    }
}
