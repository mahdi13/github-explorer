package com.perfect.githubexplorer.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @PrimaryKey val username: String,
    val id: Int
)

@Entity
data class Repository(
    @PrimaryKey val id: Int,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("stargazers_count") val stars: Int,
    @Embedded val owner: RepositoryOwner
)

data class RepositoryOwner(
    @SerializedName("login") val username: String,
    @SerializedName("avatar_url") val avatarUrl: String
)