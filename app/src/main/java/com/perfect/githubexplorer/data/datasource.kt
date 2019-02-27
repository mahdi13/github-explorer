package com.perfect.githubexplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class RepositoryDataSourceFactory(
    private val networkState : MutableLiveData<LoadingStatus>,
    private val query: String?,
    private val username: String? = null
) :
    DataSource.Factory<Int, Repository>() {

    private val sourceLiveData = MutableLiveData<RepositoryDataSource>()

    override fun create(): DataSource<Int, Repository> {
        val source = RepositoryDataSource(networkState, query, username)
        sourceLiveData.postValue(source)
        return source
    }
}

private class RepositoryDataSource(
    val networkState: MutableLiveData<LoadingStatus>,
    var query: String?,
    var username: String?
) :
    PageKeyedDataSource<Int, Repository>() {


    private val scope = CoroutineScope(Dispatchers.IO)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Repository?>) {
        networkState.postValue(LoadingStatus.LOADING)
        scope.launch {
            try {
                val result = if (username != null) {
                    apiClient.userRepositories(
                        username = username!!,
                        page = 1,
                        pageSize = params.requestedLoadSize
                    ).await()
                } else {
                    apiClient.searchRepositories(
                        query = query!!,
                        page = 1,
                        pageSize = params.requestedLoadSize
                    ).await().items
                }

                networkState.postValue(LoadingStatus.LOADED)
                callback.onResult(result, null, 2)

            } catch (e: Exception) {
                networkState.postValue(
                    LoadingStatus.FAILED
                )
            }
        }
    }


//        loadAfter(
//            params = LoadParams(0, params.requestedLoadSize), callback = object : LoadCallback<Int, Repository>() {
//                override fun onResult(data: MutableList<Repository?>, adjacentPageKey: Int?) {
////                    data += mutableListOf<Repository?>().apply {
////                        repeat(firstNullsOffset) {
////                            add(
////                                0,
////                                Repository(it.unaryMinus())
////                            )
////                        }
////                    }
//                    callback.onResult(data, null, 1)
//                }
//            }
//        )

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