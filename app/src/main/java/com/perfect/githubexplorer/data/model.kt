package com.perfect.githubexplorer.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("name") val name: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("blog") val blog: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("public_repos") val publicRepos: Int?,
    @SerializedName("followers") val followers: Int?,
    @SerializedName("following") val following: Int?,
    @SerializedName("avatar_url") val avatarUrl: String
)

data class Repository(
    val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("forks") val forks: Int?,
    @SerializedName("open_issues") val openIssues: Int?,
    @SerializedName("watchers") val watchers: Int?,
    @SerializedName("default_branch") val defaultBranch: String?,
    @SerializedName("stargazers_count") val stars: Int,
    @SerializedName("language") val language: String?,
    val owner: User
)
