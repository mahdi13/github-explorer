package com.perfect.githubexplorer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.NetworkState
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.repository_row.view.*
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.network_state_row.view.*

class RepositoryAdapter(private val glide: RequestManager, private val retryCallback: () -> Unit) :
    PagedListAdapter<Repository, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    var onRepositorySelected: ((Int) -> Unit)? = null
    var onProfileSelected: ((String) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            R.layout.repository_row -> (holder as RepositoryViewHolder).bindTo(getItem(position))
            R.layout.network_state_row -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.repository_row -> RepositoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.repository_row, parent, false)
            )
            R.layout.network_state_row -> NetworkStateViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.network_state_row, parent, false)
            )
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }


    inner class NetworkStateViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val stateView: TextView = containerView.state

        fun bindTo(networkState: NetworkState?) {
            if (networkState == null) return clear()
            stateView.text = networkState.toString()
        }

        private fun clear() {
            stateView.text = ""
        }

    }

    inner class RepositoryViewHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val nameView: TextView = containerView.name
        private val userView: Chip = containerView.user
        private val starView: TextView = containerView.stars
        private var imageLoaderTarget: Target<Drawable>? = null

        init {
            containerView.setOnClickListener {
                onRepositorySelected?.invoke(it.tag as Int)
            }

            userView.setOnClickListener {
                onProfileSelected?.invoke(it.tag as String)
            }
        }

        fun bindTo(repository: Repository?) {
            if (repository == null) return clear()

            nameView.text = repository.fullName
            userView.text = repository.owner.username
            starView.text = repository.owner.username

            containerView.tag = repository.id
            userView.tag = repository.owner.username

            userView.chipIcon = null

            glide.clear(imageLoaderTarget)
            imageLoaderTarget = glide.load(repository.owner.avatarUrl)
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

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_row
        } else {
            R.layout.repository_row
        }
    }


    override fun getItemCount(): Int = super.getItemCount() + if (hasExtraRow()) 1 else 0

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = this.networkState
        val hadExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Repository>() {
            override fun areContentsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Repository, newItem: Repository): Boolean =
                oldItem.id == newItem.id

        }

    }

}


