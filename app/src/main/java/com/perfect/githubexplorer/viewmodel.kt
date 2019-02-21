package com.perfect.githubexplorer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.perfect.githubexplorer.data.GithubRepository
import com.perfect.githubexplorer.data.Repository
import com.perfect.githubexplorer.data.User

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

class SearchViewModel : ViewModel() {
    val query: MutableLiveData<String?> = MutableLiveData()
    val page: MutableLiveData<Int?> = MutableLiveData()
    val repositories: LiveData<Repository?> = Transformations.switchMap(query) {
        val query = if (it != null) it else null
        GithubRepository.searchRepository(query = query, page = page) else null
    }

}

