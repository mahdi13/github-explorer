package com.perfect.githubexplorer.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.LoadingStatus
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.network_state_row.view.*
import kotlinx.android.synthetic.main.repository_row.view.*
import kotlinx.android.synthetic.main.user_data_row.view.*

class NetworkStateViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.network_state_row, parent, false)
    ) {
    private val stateView: TextView = itemView.state

    fun bindTo(loadingStatus: LoadingStatus?) {
        if (loadingStatus == null) return clear()
        stateView.text = loadingStatus.toString(itemView.context)
    }

    private fun clear() {
        stateView.text = ""
    }

}

class UserDataViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.user_data_row, parent, false)
    ) {
    private val keyView: TextView = itemView.key
    private val valueView: TextView = itemView.value

    fun bindTo(data: Pair<String, String>) {
        keyView.text = data.first
        valueView.text = data.second
    }
}

class RepositoryViewHolder(
    private val glide: RequestManager,
    private val onRepositorySelected: ((Int) -> Unit)?,
    private val onProfileSelected: ((String) -> Unit)? = null,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(R.layout.repository_row, parent, false)
) {
    private val nameView: TextView = itemView.name
    private val userView: Chip = itemView.user
    private val starView: TextView = itemView.stars
    private var imageLoaderTarget: Target<Drawable>? = null

    init {
        itemView.setOnClickListener {
            onRepositorySelected?.invoke(it.tag as Int)
        }

        userView.setOnClickListener {
            onProfileSelected?.invoke(it.tag as String)
        }
    }

    fun bindTo(repository: Repository?) {
        if (repository == null) return clear()

        nameView.text = repository.fullName
        userView.text = repository.owner?.username
        starView.text = repository.stars.toString()

        itemView.tag = repository.id
        userView.tag = repository.owner?.username

        userView.chipIcon = null

        glide.clear(imageLoaderTarget)
        imageLoaderTarget = glide.load(repository.owner?.avatarUrl)
            .into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    userView.chipIcon = resource
                }

            })

    }

    private fun clear() {
        nameView.text = ""
        userView.text = ""
        starView.text = ""
    }
}

fun LoadingStatus.toString(context: Context): String =
    when (this) {
        LoadingStatus.LOADING -> context.getString(R.string.loading_status)
        LoadingStatus.LOADED -> context.getString(R.string.success_status)
        LoadingStatus.FAILED -> context.getString(R.string.failed_status)
    }
