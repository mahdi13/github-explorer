package com.perfect.githubexplorer.data

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
    fun serachRepositories(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") pageSize: Int = DEFAULT_PAGE_SIZE
    ): Deferred<ArrayList<Repository>>

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

var apiClient = Retrofit.Builder()
    .baseUrl(GITHUB_BASE_API_URL)
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GithubApiInterface::class.java)
