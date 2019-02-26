package com.perfect.githubexplorer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.perfect.githubexplorer.data.LoadingStatus
import com.perfect.githubexplorer.data.Repository
import com.perfect.githubexplorer.data.SearchViewModel
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.*

import java.io.File

class ViewModelTest {

    companion object {
        private val mockServer: MockWebServer = MockWebServer()

        @BeforeClass
        @JvmStatic
        fun mockApi() {
            mockServer.start()

            mockkStatic("com.perfect.githubexplorer.data.WebKt")
            every {
                Class.forName("com.perfect.githubexplorer.data.WebKt")
                    .getMethod("getGITHUB_BASE_API_URL")
                    .invoke(this)
            } returns mockServer.url("/").toString()

        }

        @AfterClass
        @JvmStatic
        fun apiShutdown() {
            mockServer.shutdown()
        }
    }

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
    }

    @Test
    fun testSearchViewModel() {
        //    fun mockApi() {
//        val behavior = NetworkBehavior.create()
//        behavior.setDelay(3.toLong(), TimeUnit.SECONDS)
//        val mockRetrofit = MockRetrofit.Builder(apiRetrofit)
//            .networkBehavior(behavior)
//            .build()
//
//        val delegate = mockRetrofit.create(GithubApiInterface::class.java)
//        val mockApiClient = MockGitHub(delegate)
//
//
//        mockkObject(apiRetrofit)
//
//        every { apiRetrofit.create(GithubApiInterface::class.java) } returns mockApiClient
//
//    }

        repeat(3) { i ->
            val path = "github-search-mockq-p${i + 1}.json"
            mockServer.enqueue(
                MockResponse().setBody(
                    Buffer().readFrom(
                        File(Thread.currentThread().contextClassLoader.getResource(path).path).inputStream()
                    )
                )
            )
        }

        viewModel.loadingState.observeForever(loadingStateObserver)
        viewModel.repositories.observeForever(repositoriesObserver)
        viewModel.query.value = "mockq"

        Thread.sleep(1000)
        verify {
            repositoriesObserver.onChanged(match {
                it.loadedCount == 10
            })
        }


        viewModel.repositories.value!!.loadAround(9)
        Thread.sleep(1000)
        viewModel.repositories.value!!.loadAround(19)
        Thread.sleep(1000)

        verifyOrder {
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            loadingStateObserver.onChanged(LoadingStatus.LOADED)
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            loadingStateObserver.onChanged(LoadingStatus.LOADED)
            loadingStateObserver.onChanged(LoadingStatus.LOADING)
            loadingStateObserver.onChanged(LoadingStatus.LOADED)
        }

        verify {
            repositoriesObserver.onChanged(match {
                it.loadedCount == 26
            })
        }

    }
}
