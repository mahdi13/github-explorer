package com.perfect.githubexplorer.data

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("login") val username: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("company") val company: String? = null,
    @SerializedName("blog") val blog: String? = null,
    @SerializedName("location") val location: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("bio") val bio: String? = null,
    @SerializedName("public_repos") val publicRepos: Int? = null,
    @SerializedName("followers") val followers: Int? = null,
    @SerializedName("following") val following: Int? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null
) {
    companion object {
        const val RECORDS_TO_SHOW = 6
    }
}

data class Repository(
    val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("forks") val forks: Int? = null,
    @SerializedName("open_issues") val openIssues: Int? = null,
    @SerializedName("default_branch") val defaultBranch: String? = null,
    @SerializedName("stargazers_count") val stars: Int = 0,
    @SerializedName("language") val language: String? = null,
    val owner: User? = null
)
