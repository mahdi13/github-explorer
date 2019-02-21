package com.perfect.githubexplorer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val username: String,
    val id: Int
)

@Entity
data class Repository(
    @PrimaryKey val id: Int
)