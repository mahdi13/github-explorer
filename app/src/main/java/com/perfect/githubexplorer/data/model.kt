package com.perfect.githubexplorer.data

import androidx.room.Entity

@Entity
data class User(
    val id: Int
)

@Entity
data class Repository(
    val id: Int
)