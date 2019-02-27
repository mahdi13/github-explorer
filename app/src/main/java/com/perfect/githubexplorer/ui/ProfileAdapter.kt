package com.perfect.githubexplorer.ui

import android.content.Context
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.LoadingStatus
import com.perfect.githubexplorer.data.Repository
import com.perfect.githubexplorer.data.User


class ProfileAdapter(private val glide: RequestManager, private val user: User?, context: Context) :
    PagedListAdapter<Any, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var loadingStatus: LoadingStatus? = null

    var onRepositorySelected: ((Int) -> Unit)? = null

    private val userRecords: List<Pair<String, String>> = listOf(
        Pair(context.getString(R.string.username), user?.username ?: context.getString(R.string.not_available)),
        Pair(context.getString(R.string.email), user?.email ?: context.getString(R.string.not_available)),
        Pair(context.getString(R.string.company), user?.company ?: context.getString(R.string.not_available)),
        Pair(context.getString(R.string.location), user?.location ?: context.getString(R.string.not_available)),
        Pair(context.getString(R.string.bio), user?.bio ?: context.getString(R.string.not_available)),
        Pair(
            context.getString(R.string.followers),
            user?.followers?.toString() ?: context.getString(R.string.not_available)
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            R.layout.user_data_row -> (holder as UserDataViewHolder)
                .bindTo(userRecords[position])
            R.layout.repository_row -> (holder as RepositoryViewHolder).bindTo(this.getItem(position) as Repository)
            R.layout.network_state_row -> (holder as NetworkStateViewHolder).bindTo(loadingStatus)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1) {
            R.layout.network_state_row
        } else if (getItem(position) is Repository) {
            R.layout.repository_row
        } else {
            R.layout.user_data_row
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.network_state_row -> NetworkStateViewHolder(parent)
            R.layout.user_data_row -> UserDataViewHolder(parent)
            else -> RepositoryViewHolder(glide, onRepositorySelected, null, parent)
        }

    override fun getItemCount(): Int = super.getItemCount() + (if (hasExtraRow()) 1 else 0)

    private fun hasExtraRow() = loadingStatus != null && loadingStatus != LoadingStatus.LOADED

    fun setNetworkState(newLoadingStatus: LoadingStatus?) {
        val previousState = this.loadingStatus
        val hadExtraRow = hasExtraRow()
        this.loadingStatus = newLoadingStatus
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newLoadingStatus) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Any>() {
            override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean =
                oldItem as Repository == newItem as Repository

            override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean =
                (oldItem as Repository).id == (newItem as Repository).id

        }

    }

}
