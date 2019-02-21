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
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.Repository

class RepositoryAdapter : PagedListAdapter<Repository, RepositoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val repository = getItem(position)
        if (repository != null) {
            holder.bindTo(repository)
        } else {
            // Null defines a placeholder item - PagedListAdapter automatically
            // invalidates this row when the actual object is loaded from the
            // database.
            holder.clear()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_row, parent, false)

        return ViewHolder(view)

    }


    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
//        val amountView: TextView = view.amount
//        val priceView: TextView = view.price
//        val changeView: TextView = view.change

        fun bindTo(repository: Repository) {
            // TODO
        }

        fun clear() {
            // TODO
        }
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<Repository>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                oldRepository: Repository,
                newRepository: Repository
            ): Boolean =
                oldRepository.id == newRepository.id

            override fun areContentsTheSame(
                oldRepository: Repository,
                newRepository: Repository
            ): Boolean =
                oldRepository == newRepository
        }
    }

}


