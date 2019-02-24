package com.perfect.githubexplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RepositoryDataSourceFactory(private val query: String?, private val username: String? = null) :
    DataSource.Factory<Int, Repository>() {

    private val sourceLiveData = MutableLiveData<RepositoryDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = RepositoryDataSource(query, username)
        sourceLiveData.postValue(source)
        return source
    }
}

class RepositoryDataSource(var query: String?, var username: String?) : PageKeyedDataSource<Int, Repository>() {

    val networkState = MutableLiveData<LoadingStatus>()

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Repository>) {
        networkState.postValue(LoadingStatus.LOADING)

        scope.launch {
            try {
                val result = if (username != null) {
                    apiClient.userRepositories(
                        username = username!!,
                        page = 0,
                        pageSize = params.requestedLoadSize
                    ).await()
                } else {
                    apiClient.searchRepositories(
                        query = query!!,
                        page = 0,
                        pageSize = params.requestedLoadSize
                    ).await().items
                }

                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(result, null, 1)

            } catch (e: Exception) {
                val error = LoadingStatus.FAILED
                networkState.postValue(error)
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {
                val result = if (username != null) {
                    apiClient.userRepositories(
                        username = username!!,
                        page = params.key,
                        pageSize = params.requestedLoadSize
                    ).await()
                } else {
                    apiClient.searchRepositories(
                        query = query!!,
                        page = params.key,
                        pageSize = params.requestedLoadSize
                    ).await().items
                }

                callback.onResult(result, params.key + 1)
                networkState.postValue(LoadingStatus.LOADED)

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Repository>) = Unit

}