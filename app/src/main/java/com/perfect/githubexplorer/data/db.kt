package com.perfect.githubexplorer.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Repository::class
    ],
    version = 1
)
abstract class GithubDatabase : RoomDatabase() {
    abstract val repositoryDao: RepositoryDao
    abstract val userDao: UserDao
}

lateinit var githubDatabase: GithubDatabase

object GithubRepository {
    private val repositoryDao = githubDatabase.repositoryDao
    private val userDao = githubDatabase.userDao

    private val scope = CoroutineScope(Dispatchers.Default)

    fun loadUser(username: String): LiveData<User> {
        scope.launch { userDao.update(apiClient.userProfile(username).await()) }
        return userDao.load(username)
    }

    fun loadRepository(id: Int): LiveData<Repository> {
        scope.launch { repositoryDao.update(apiClient.repositoryDetail(id).await()) }
        return repositoryDao.load(id)
    }

//    fun searchRepository(query: String, page: Int = 0): LiveData<ArrayList<Repository>> {
//        scope.run {
//            repositoryDao.update(apiClient.serachRepositories().await())
//        }
//    }
}

@Dao
interface RepositoryDao {
    @Insert(onConflict = REPLACE)
    fun save(repository: Repository)

    @Update(onConflict = REPLACE)
    fun update(repository: Repository)

    @Query("SELECT * FROM Repository WHERE id = :id")
    fun load(id: Int): LiveData<Repository>

    @Query("SELECT * FROM Repository")
    fun loadPaging(): DataSource.Factory<Int, Repository>

    @Query("SELECT * FROM Repository")
    fun loadAll(): LiveData<List<Repository>>

    @Query("DELETE FROM Repository")
    fun deleteAll()
}

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Update(onConflict = REPLACE)
    fun update(user: User)

    @Query("SELECT * FROM User WHERE username = :username")
    fun load(username: String): LiveData<User>

    @Query("SELECT * FROM User")
    fun loadAll(): LiveData<List<User>>

    @Query("DELETE FROM User")
    fun deleteAll()
}