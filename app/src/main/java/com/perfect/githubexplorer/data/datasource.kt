package com.perfect.githubexplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RepositoryDataSourceFactory(
    private val query: String?,
    private val username: String? = null,
    private val firstNullsOffset: Int = 0
) :
    DataSource.Factory<Int, Repository>() {

    private val sourceLiveData = MutableLiveData<RepositoryDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = RepositoryDataSource(query, username, firstNullsOffset)
        sourceLiveData.postValue(source)
        return source
    }
}

class RepositoryDataSource(var query: String?, var username: String?, private val firstNullsOffset: Int) :
    PageKeyedDataSource<Int, Repository>() {

    val networkState = MutableLiveData<LoadingStatus>()

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Repository?>) =
        loadAfter(
            params = LoadParams(0, params.requestedLoadSize), callback = object : LoadCallback<Int, Repository>() {
                override fun onResult(data: MutableList<Repository?>, adjacentPageKey: Int?) {
                    data += mutableListOf<Repository?>().apply {
                        repeat(firstNullsOffset) {
                            add(
                                0,
                                Repository(it.unaryMinus())
                            )
                        }
                    }
                    callback.onResult(data, null, 1)
                }
            }
        )

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