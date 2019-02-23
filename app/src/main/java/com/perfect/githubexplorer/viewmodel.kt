package com.perfect.githubexplorer

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.perfect.githubexplorer.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    val username: MutableLiveData<String?> = MutableLiveData()

    val user: LiveData<User?> = Transformations.switchMap(username) { newUsername ->
        object : LiveData<User>() {
            init {
                newUsername?.let {
                    CoroutineScope(Dispatchers.Default).launch {
                        postValue(
                            apiClient.userProfile(
                                newUsername
                            ).await()
                        )
                    }
                }
            }
        }
    }

//    val userRepositories: LiveData<Repository?> = Transformations.switchMap(username) {
//        if (it != null) GithubRepository.loadRepository(it) else null
//    }
}

class RepositoryViewModel : ViewModel() {
    val repositoryId: MutableLiveData<Int?> = MutableLiveData()
//    val repository: LiveData<Repository?> = Transformations.switchMap(repositoryId) {
//        if (it != null) GithubRepository.loadRepository(it) else null
//    }
}

class SearchViewModel : ViewModel() {
    val query: MutableLiveData<String?> = MutableLiveData()
    val networkState: MutableLiveData<NetworkState> = MutableLiveData()
    val repositories: LiveData<PagedList<Repository>> = Transformations.switchMap(query) { query ->

        val sourceFactory = RepositoryDataSourceFactory(query ?: "")

        sourceFactory.toLiveData(pageSize = DEFAULT_PAGE_SIZE)

//        val livePagedList = sourceFactory.toLiveData(pageSize = DEFAULT_PAGE_SIZE)
//
//        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) {
//            it.initialLoad
//        }
//        Listing(
//            pagedList = livePagedList,
//            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
//                it.networkState
//            },
//            retry = {
//                sourceFactory.sourceLiveData.value?.retryAllFailed()
//            },
//            refresh = {
//                sourceFactory.sourceLiveData.value?.invalidate()
//            },
//            refreshState = refreshState
//        )
//
//        LivePagedListBuilder(repositoryDao.loadPaging(), DEFAULT_PAGE_SIZE).build()
    }

}

