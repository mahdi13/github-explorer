package com.perfect.githubexplorer.data

import android.content.Context
import com.google.gson.annotations.SerializedName
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.perfect.githubexplorer.R
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val GITHUB_BASE_API_URL = "https://api.github.com"
const val GITHUB_MARKDOWN_URL = "https://raw.githubusercontent.com/%s/%s/README.md"
const val DEFAULT_PAGE_SIZE = 20

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
        @Path("username") username: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = DEFAULT_PAGE_SIZE
    ): Deferred<List<Repository>>

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

enum class LoadingStatus {
    LOADING,
    LOADED,
    FAILED;

    fun toString(context: Context): String =
        when (this) {
            LoadingStatus.LOADING -> context.getString(R.string.loading_status)
            LoadingStatus.LOADED -> context.getString(R.string.success_status)
            LoadingStatus.FAILED -> context.getString(R.string.failed_status)
        }
}

var apiClient: GithubApiInterface = Retrofit.Builder()
    .baseUrl(GITHUB_BASE_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GithubApiInterface::class.java)
