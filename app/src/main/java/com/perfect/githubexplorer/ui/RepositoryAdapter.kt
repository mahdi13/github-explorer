package com.perfect.githubexplorer.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.NetworkState
import com.perfect.githubexplorer.data.Repository

class RepositoryAdapter(private val glide: RequestManager) :
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
            R.layout.repository_row -> RepositoryViewHolder(glide, onRepositorySelected, onProfileSelected, parent)
            R.layout.network_state_row -> NetworkStateViewHolder(parent)
            else -> throw IllegalArgumentException("unknown view type $viewType")
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


