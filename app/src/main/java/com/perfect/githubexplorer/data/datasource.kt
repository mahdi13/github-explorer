package com.perfect.githubexplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.perfect.githubexplorer.data.User.Companion.RECORDS_TO_SHOW
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RepositoryDataSourceFactory(
    private val networkState: MutableLiveData<LoadingStatus>,
    private val query: String?
) :
    DataSource.Factory<Int, Repository>() {

    private val sourceLiveData = MutableLiveData<RepositoryDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = RepositoryDataSource(networkState, query)
        sourceLiveData.postValue(source)
        return source
    }
}

class UserRepositoryDataSourceFactory(
    private val networkState: MutableLiveData<LoadingStatus>,
    private val username: String? = null
) :
    DataSource.Factory<Int, Any>() {

    private val sourceLiveData = MutableLiveData<UserRepositoryDataSource>()

    override fun create(): DataSource<Int, Any> {
        val source = UserRepositoryDataSource(networkState, username)
        sourceLiveData.postValue(source)
        return source
    }
}

private class RepositoryDataSource(
    val networkState: MutableLiveData<LoadingStatus>,
    var query: String?
) :
    PageKeyedDataSource<Int, Repository>() {


    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Repository?>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {
                val result =
                    apiClient.searchRepositories(
                        query = query!!,
                        page = 1,
                        pageSize = params.requestedLoadSize
                    ).await().items
                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(result, null, 2)

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {
                val result =
                    apiClient.searchRepositories(
                        query = query!!,
                        page = params.key,
                        pageSize = params.requestedLoadSize
                    ).await().items

                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(result, params.key + 1)

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) = Unit

}

private class UserRepositoryDataSource(
    val networkState: MutableLiveData<LoadingStatus>,
    var username: String?
) :
    PageKeyedDataSource<Int, Any>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Any>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {

                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(
                    // Some empty records which should be filled with proper title and value in the related adapter
                    mutableListOf<Any>().apply { repeat(RECORDS_TO_SHOW) { i -> add(Pair(null, null)) } },
                    null,
                    1
                )

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }
    }


    private val scope = CoroutineScope(Dispatchers.IO)


    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {
                val result = apiClient.userRepositories(
                    username = username!!,
                    page = params.key,
                    pageSize = params.requestedLoadSize
                ).await()


                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(result, params.key + 1)

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Any>) = Unit

}