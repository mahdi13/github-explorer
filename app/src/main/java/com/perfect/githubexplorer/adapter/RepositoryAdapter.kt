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
import com.perfect.githubexplorer.MainActivity
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.NetworkState
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.repository_row.view.*
import org.jetbrains.anko.startActivity
import android.os.Build
import android.graphics.drawable.Drawable
import androidx.transition.Transition
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import kotlinx.coroutines.*
import okhttp3.Dispatcher


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


    inner class ViewHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {
        private val nameView: TextView = containerView.name
        private val userView: Chip = containerView.user
        private val starView: TextView = containerView.stars
        private var imageLoaderTarget: Target<Drawable>? = null

        init {
            containerView.setOnClickListener {
                it.context.startActivity<MainActivity>(
                    "id" to (it.tag as String)
                )
            }

            userView.setOnClickListener {
                it.context.startActivity<MainActivity>(
                    "username" to (it.tag as String)
                )
            }
        }

        fun bindTo(repository: Repository) {
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


//            glide.asDrawable().load(repository.owner.avatarUrl)..listener(object : RequestListener<Drawable> {
//                override fun onLoadFailed(
//                    e: GlideException?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    return true
//                }
//
//                override fun onResourceReady(
//                    resource: Drawable?,
//                    model: Any?,
//                    target: Target<Drawable>?,
//                    dataSource: DataSource?,
//                    isFirstResource: Boolean
//                ): Boolean {
//                    userView.chipIcon = resource
//                    return true
//                }
//
//            })

//            imageLoader = GlobalScope.launch(Dispatchers.IO) {
//                if (imageLoader != null && imageLoader!!.isActive) imageLoader!!.cancelAndJoin()
//                val d = glide.asDrawable().load(repository.owner.avatarUrl).listener(object: RequestListener<Drawable>{
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        return true
//                    }
//
//                    override fun onResourceReady(
//                        resource: Drawable?,
//                        model: Any?,
//                        target: Target<Drawable>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                       return true
//                    }
//
//                })
//                GlobalScope.launch(Dispatchers.Main) { userView.chipIcon = d }
//        }
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


