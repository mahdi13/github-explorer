package com.perfect.githubexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.perfect.githubexplorer.data.*

class ProfileViewModel : ViewModel() {
    val username: MutableLiveData<String?> = MutableLiveData()

    val user: LiveData<User?> = Transformations.switchMap(username) {
        if (it != null) GithubRepository.loadUser(it) else null
    }

//    val userRepositories: LiveData<Repository?> = Transformations.switchMap(username) {
//        if (it != null) GithubRepository.loadRepository(it) else null
//    }
}

class RepositoryViewModel : ViewModel() {
    val repositoryId: MutableLiveData<Int?> = MutableLiveData()
    val repository: LiveData<Repository?> = Transformations.switchMap(repositoryId) {
        if (it != null) GithubRepository.loadRepository(it) else null
    }
}

class SearchViewModel(val repositoryDao: RepositoryDao) : ViewModel() {
    val query: MutableLiveData<String?> = MutableLiveData()
    val page: MutableLiveData<Int?> = MutableLiveData()
    val repositories: LiveData<PagedList<Repository>> = Transformations.switchMap(query) {
        repositoryDao.deleteAll()
        LivePagedListBuilder(repositoryDao.loadPaging(), DEFAULT_PAGE_SIZE).build()
    }

}

