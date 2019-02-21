package com.perfect.githubexplorer.data

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

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

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

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

    @Query("SELECT * FROM User WHERE id = :id")
    fun load(id: Int): LiveData<User>

    @Query("SELECT * FROM User")
    fun loadAll(): LiveData<List<User>>

    @Query("DELETE FROM User")
    fun deleteAll()
}