package com.perfect.githubexplorer.adapter

import android.annotation.SuppressLint
import android.graphics.Color
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

class RepositoryAdapter(private val glide: RequestManager, private val retryCallback: () -> Unit) :
    PagedListAdapter<Repository, RepositoryAdapter.ViewHolder>(POST_COMPARATOR) {

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = getItem(position)
        if (repository != null) {
            holder.bindTo(repository)
        } else {
            holder.clear()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_row, parent, false)

        return ViewHolder(view)

    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.name
        val userView: TextView = view.user
        val starView: TextView = view.stars

        fun bindTo(repository: Repository) {
            nameView.text = repository.fullName
            userView.text = repository.owner.username
            starView.text = repository.owner.username
        }

        fun clear() {
            nameView.text = ""
            userView.text = ""
            starView.text = ""
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


