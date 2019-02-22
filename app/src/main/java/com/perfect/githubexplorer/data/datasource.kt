package com.perfect.githubexplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RepositoryDataSourceFactory(private val query: String) : DataSource.Factory<Int, Repository>() {

    val sourceLiveData = MutableLiveData<RepositoryDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = RepositoryDataSource(query)
        sourceLiveData.postValue(source)
        return source
    }
}

class RepositoryDataSource(var query: String) : PageKeyedDataSource<Int, Repository>() {

    val networkState = MutableLiveData<NetworkState>()
    val initialLoad = MutableLiveData<NetworkState>()

    private val scope = CoroutineScope(Dispatchers.Default)

    private var retry: (() -> Any)? = null

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            scope.launch {
                it.invoke()
            }
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Repository>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        scope.launch {
            try {
                val result = apiClient.searchRepositories(
                    query = query,
                    page = 0,
                    pageSize = params.requestedLoadSize
                ).await()
                retry = null
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                callback.onResult(result.items, null, 1)

            } catch (e: Exception) {
                retry = {
                    loadInitial(params, callback)
                }
                val error = NetworkState.error(e.message ?: "unknown error")
                networkState.postValue(error)
                initialLoad.postValue(error)

            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) {
        networkState.postValue(NetworkState.LOADING)
        scope.launch {
            try {
                val result = apiClient.searchRepositories(
                    query = query,
                    page = params.key,
                    pageSize = params.requestedLoadSize
                ).await()
                retry = null
                callback.onResult(result.items, params.key + 1)
                networkState.postValue(NetworkState.LOADED)

            } catch (e: Exception) {
                retry = {
                    loadAfter(params, callback)
                }
                networkState.postValue(
                    NetworkState.error(e.message)
                )
            }
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) = Unit

}