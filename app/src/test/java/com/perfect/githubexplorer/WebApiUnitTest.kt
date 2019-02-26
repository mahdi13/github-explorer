package com.perfect.githubexplorer

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perfect.githubexplorer.data.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.MockResponse
import okio.Buffer
import org.junit.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class WebApiUnitTest {

    companion object {
        lateinit var mockServer: MockWebServer

        @BeforeClass
        @JvmStatic
        fun mockApi() {
            mockServer = MockWebServer()
            mockServer.start()
        }

        @AfterClass
        @JvmStatic
        fun apiShutdown() {
            mockServer.shutdown()
        }
    }

    @Before
    fun setup() {
        mockkObject(lazyApiClient)
        every { apiClient } returns Retrofit.Builder()
            .baseUrl(mockServer.url("/").toString())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GithubApiInterface::class.java)
    }

    @After
    fun after() {
        unmockkAll()
    }

    @Test
    fun testRepositorySearch() {
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

        val page1: SearchRepositoryResponse = runBlocking {
            apiClient.searchRepositories("mockq", 1, 10).await()
        }

        assertEquals(26, page1.totalCount)
        assertEquals(false, page1.incompleteResults)
        assertEquals(10, page1.items.size)

        assertEquals(115428922, page1.items[0].id)
        assertEquals(6, page1.items[0].forks)
        assertEquals("master", page1.items[0].defaultBranch)
        assertEquals("romantitov/MockQueryable", page1.items[0].fullName)
        assertEquals("C#", page1.items[0].language)
        assertEquals("MockQueryable", page1.items[0].name)
        assertEquals(0, page1.items[0].openIssues)
        assertEquals(35, page1.items[0].stars)
        assertEquals(11888679, page1.items[0].owner!!.id)
        assertEquals("romantitov", page1.items[0].owner!!.username)
        assertEquals("https://avatars1.githubusercontent.com/u/11888679?v=4", page1.items[0].owner!!.avatarUrl)

        // Not contains in the repository search's response
        assertNull(page1.items[0].owner!!.name)
        assertNull(page1.items[0].owner!!.publicRepos)
        assertNull(page1.items[0].owner!!.bio)
        assertNull(page1.items[0].owner!!.blog)
        assertNull(page1.items[0].owner!!.company)
        assertNull(page1.items[0].owner!!.email)
        assertNull(page1.items[0].owner!!.location)
        assertNull(page1.items[0].owner!!.followers)
        assertNull(page1.items[0].owner!!.following)

        val page2: SearchRepositoryResponse = runBlocking {
            apiClient.searchRepositories("mockq", 2, 10).await()
        }

        assertEquals(10, page2.items.size)
        assertEquals(129738648, page2.items[0].id)

        val page3: SearchRepositoryResponse = runBlocking {
            apiClient.searchRepositories("mockq", 3, 10).await()
        }

        assertEquals(6, page3.items.size)
    }


    @Test
    fun testUserProfile() {
        val path = "github-user-profile-henryr.json"
        mockServer.enqueue(
            MockResponse().setBody(
                Buffer().readFrom(
                    File(Thread.currentThread().contextClassLoader.getResource(path).path).inputStream()
                )
            )
        )

        val response: User = runBlocking {
            apiClient.userProfile("henryr").await()
        }

        assertEquals(89301, response.id)
        assertEquals("henryr", response.username)
        assertEquals("https://avatars0.githubusercontent.com/u/89301?v=4", response.avatarUrl)
        assertEquals("Henry Robinson", response.name)
        assertEquals(29, response.publicRepos)
        assertNull(response.bio)
        assertEquals("http://the-paper-trail.org/blog/", response.blog)
        assertNull(response.company)
        assertNull(response.email)
        assertEquals("San Francisco, CA", response.location)
        assertEquals(203, response.followers)
        assertEquals(4, response.following)
    }

    @Test
    fun testUserRepositories() {
        repeat(3) { i ->
            val path = "github-user-repos-henryr-p${i + 1}.json"
            mockServer.enqueue(
                MockResponse().setBody(
                    Buffer().readFrom(
                        File(Thread.currentThread().contextClassLoader.getResource(path).path).inputStream()
                    )
                )
            )
        }

        val page1: List<Repository> = runBlocking {
            apiClient.userRepositories("henryr", 1, 10).await()
        }

        assertEquals(10, page1.size)

        assertEquals(9972588, page1[0].id)
        assertEquals(46, page1[0].forks)
        assertEquals("master", page1[0].defaultBranch)
        assertEquals("henryr/cap-faq", page1[0].fullName)
        assertNull(page1[0].language)
        assertEquals("cap-faq", page1[0].name)
        assertEquals(5, page1[0].openIssues)
        assertEquals(480, page1[0].stars)
        assertEquals(89301, page1[0].owner!!.id)
        assertEquals("henryr", page1[0].owner!!.username)
        assertEquals("https://avatars0.githubusercontent.com/u/89301?v=4", page1[0].owner!!.avatarUrl)

        // Not contains in the user repositories response
        assertNull(page1[0].owner!!.name)
        assertNull(page1[0].owner!!.publicRepos)
        assertNull(page1[0].owner!!.bio)
        assertNull(page1[0].owner!!.blog)
        assertNull(page1[0].owner!!.company)
        assertNull(page1[0].owner!!.email)
        assertNull(page1[0].owner!!.location)
        assertNull(page1[0].owner!!.followers)
        assertNull(page1[0].owner!!.following)

        val page2: List<Repository> = runBlocking {
            apiClient.userRepositories("henryr", 2, 10).await()
        }

        assertEquals(10, page2.size)

        val page3: List<Repository> = runBlocking {
            apiClient.userRepositories("henryr", 3, 10).await()
        }

        assertEquals(9, page3.size)
    }

    @Test
    fun testRepositoryDetail() {
        val path = "github-repository-cap-faq.json"
        mockServer.enqueue(
            MockResponse().setBody(
                Buffer().readFrom(
                    File(Thread.currentThread().contextClassLoader.getResource(path).path).inputStream()
                )
            )
        )

        val response: Repository = runBlocking {
            apiClient.repositoryDetail(9972588).await()
        }

        assertEquals(9972588, response.id)
        assertEquals(46, response.forks)
        assertEquals("master", response.defaultBranch)
        assertEquals("henryr/cap-faq", response.fullName)
        assertNull(response.language)
        assertEquals("cap-faq", response.name)
        assertEquals(5, response.openIssues)
        assertEquals(480, response.stars)
        assertEquals(89301, response.owner!!.id)
        assertEquals("henryr", response.owner!!.username)
        assertEquals("https://avatars0.githubusercontent.com/u/89301?v=4", response.owner!!.avatarUrl)

        // Not contains in the repository detail response
        assertNull(response.owner!!.name)
        assertNull(response.owner!!.publicRepos)
        assertNull(response.owner!!.bio)
        assertNull(response.owner!!.blog)
        assertNull(response.owner!!.company)
        assertNull(response.owner!!.email)
        assertNull(response.owner!!.location)
        assertNull(response.owner!!.followers)
        assertNull(response.owner!!.following)
    }


}
