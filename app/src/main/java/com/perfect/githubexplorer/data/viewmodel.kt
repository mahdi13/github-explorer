package com.perfect.githubexplorer.data

import androidx.lifecycle.*
import androidx.paging.PagedList
import androidx.paging.toLiveData
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


    val repositories: LiveData<PagedList<Repository>> = Transformations.switchMap(user) { newUser ->
        if (newUser == null) return@switchMap null

        val sourceFactory = RepositoryDataSourceFactory(loadingStatus, null, newUser.username)
        sourceFactory.toLiveData(pageSize = DEFAULT_PAGE_SIZE)
    }
    val loadingStatus: MutableLiveData<LoadingStatus> = MutableLiveData()

}

class RepositoryViewModel : ViewModel() {
    val repositoryId: MutableLiveData<Int?> = MutableLiveData()

    val repository: LiveData<Repository?> = Transformations.switchMap(repositoryId) { newRepositoryId ->
        object : LiveData<Repository>() {
            init {
                newRepositoryId?.let {
                    CoroutineScope(Dispatchers.Default).launch {
                        postValue(
                            apiClient.repositoryDetail(
                                newRepositoryId
                            ).await()
                        )
                    }
                }
            }
        }
    }
}

class SearchViewModel : ViewModel() {
    val query: MutableLiveData<String?> = MutableLiveData()
    val repositories: LiveData<PagedList<Repository>> = Transformations.switchMap(query) { query ->
        val sourceFactory = RepositoryDataSourceFactory(loadingStatus, query ?: "")
        sourceFactory.toLiveData(pageSize = DEFAULT_PAGE_SIZE)
    }
    val loadingStatus: MutableLiveData<LoadingStatus> = MutableLiveData()

}

