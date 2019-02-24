package com.perfect.githubexplorer.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.NetworkState
import com.perfect.githubexplorer.data.Repository


class ProfileAdapter(
    private val glide: RequestManager,
    private val userDataList: List<Pair<String, String>>
) :
    PagedListAdapter<Repository, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    var onRepositorySelected: ((Int) -> Unit)? = null


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.user_data_row -> (holder as UserDataViewHolder).bindTo(userDataList[position])
            R.layout.repository_row -> (holder as RepositoryViewHolder).bindTo(getItem(position))
            R.layout.network_state_row -> (holder as NetworkStateViewHolder).bindTo(networkState)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_row
        } else if (position < userDataList.size) {
            R.layout.user_data_row
        } else {
            R.layout.repository_row
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.network_state_row -> UserDataViewHolder(parent)
            R.layout.user_data_row -> UserDataViewHolder(parent)
            else -> RepositoryViewHolder(glide, onRepositorySelected, null, parent)
        }


    override fun getItemCount(): Int = super.getItemCount() + userDataList.size + if (hasExtraRow()) 1 else 0

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


