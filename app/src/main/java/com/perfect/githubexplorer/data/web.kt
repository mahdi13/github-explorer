package com.perfect.githubexplorer.data

import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val GITHUB_BASE_API_URL = "https://api.github.com"
const val DEFAULT_PAGE_SIZE = 1

@Suppress("DeferredIsResult")
interface GithubApiInterface {

    @GET("search/repositories")
    fun searchRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = DEFAULT_PAGE_SIZE
    ): Deferred<SearchRepositoryResponse>

    @GET("users/{username}")
    fun userProfile(
        @Path("username") username: String
    ): Deferred<User>

    @GET("users/{username}/repos")
    fun userRepositories(
        @Path("username") username: String
    ): Deferred<ArrayList<User>>

    @GET("repositories/{id}")
    fun repositoryDetail(
        @Path("id") id: Int
    ): Deferred<Repository>

}

data class SearchRepositoryResponse(
    @SerializedName("items") val items: List<Repository>,
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("incomplete_results") val incompleteResults: Boolean
)

enum class NetworkStatus {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: NetworkStatus,
    val msg: String? = null
) {
    companion object {
        val LOADED = NetworkState(NetworkStatus.SUCCESS)
        val LOADING = NetworkState(NetworkStatus.RUNNING)
        fun error(msg: String?) = NetworkState(NetworkStatus.FAILED, msg)
    }
}

var apiClient = Retrofit.Builder()
    .baseUrl(GITHUB_BASE_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GithubApiInterface::class.java)
