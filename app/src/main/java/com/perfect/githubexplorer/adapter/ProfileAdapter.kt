package com.perfect.githubexplorer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.NetworkState
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.repository_row.view.*
import com.perfect.githubexplorer.data.User
import kotlinx.android.synthetic.main.user_data_row.view.*

val SHOWING_DATA_COUNT = 4

class ProfileAdapter(val user: User, private val retryCallback: () -> Unit) :
    PagedListAdapter<Repository, RecyclerView.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    var onRepositorySelected: ((Long) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            0 -> {
                holder as UserDataViewHolder
                when (position) {
                    0 -> holder.bindTo("Email", user.username)
                    1 -> holder.bindTo("Email", user.username)
                    2 -> holder.bindTo("Email", user.username)
                    3 -> holder.bindTo("Email", user.username)
                    else -> holder.bindTo("", "")
                }
            }
            else -> {
                holder as RepositoryViewHolder
                val repository = getItem(position - SHOWING_DATA_COUNT)
                if (repository != null) {
                    holder.bindTo(repository)
                } else {
                    holder.clear()
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int = if (position < SHOWING_DATA_COUNT) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> UserDataViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_data_row, parent, false)
            )
            else -> RepositoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.repository_row, parent, false)
            )
        }


    inner class UserDataViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val keyView: TextView = containerView.key
        private val valueView: TextView = containerView.value

        fun bindTo(key: String, value: String) {
            keyView.text = key
            valueView.text = value
        }
    }

    inner class RepositoryViewHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val nameView: TextView = containerView.name
        private val starView: TextView = containerView.stars

        init {
            containerView.user.visibility = View.GONE
            containerView.setOnClickListener {
                onRepositorySelected?.invoke((it.tag as String).toLong())
            }
        }

        fun bindTo(repository: Repository) {
            nameView.text = repository.fullName
            starView.text = repository.owner.username
            containerView.tag = repository.id
        }

        fun clear() {
            nameView.text = ""
            starView.text = ""
        }
    }


    override fun getItemCount(): Int = super.getItemCount() + SHOWING_DATA_COUNT + if (hasExtraRow()) 1 else 0

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


