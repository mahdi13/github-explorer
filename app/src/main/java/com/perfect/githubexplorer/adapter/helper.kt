package com.perfect.githubexplorer.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import retrofit2.HttpException

import java.util.ArrayList

interface OnItemLoaded<T> {
    fun itemLoaded(allItems: List<T>, newItems: List<T>)
}

abstract class EndlessRecyclerAdapter<ViewHolder : RecyclerView.ViewHolder, T> : RecyclerView.Adapter<ViewHolder>() {

    private var page: Int = -1
    open val items: ArrayList<T> = ArrayList()
    private var job: Job? = null
    private var finish = false
    var loadDelegation: OnItemLoaded<T>? = null

    private fun doLoadItemJob() = GlobalScope.launch(Dispatchers.Main) {
        try {
            val newItems = loadPage(page + 1)
            if (newItems.isEmpty()) {
                //It is the last page, stop!
                finish = true
                if (items.isEmpty()) listFinishedEmpty()
            } else {
                // Append and go on!
                page += 1
                items.addAll(newItems)
                loadDelegation?.itemLoaded(items, newItems)
                notifyDataSetChanged()
            }
        } catch (e: HttpException) {
            // Network error
            delay(1000)
        }
    }

    open fun listFinishedEmpty() {}

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        job = doLoadItemJob()
    }

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!finish && items.size - position < 5 && job?.isActive != true) {
            // Load next page:
            job = doLoadItemJob()
        }
    }

    abstract suspend fun loadPage(page: Int): List<T>

    /**
     * Clean the adapter.
     * ALWAYS call this method before destroying the adapter to remove the listener.
     */
    fun destroy() {
        if (job?.isActive == true) job!!.cancel()
    }

    override fun getItemCount() = items.size
}
