package com.perfect.githubexplorer.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.perfect.githubexplorer.R
import com.perfect.githubexplorer.data.Repository

class RepositoryAdapter : EndlessRecyclerAdapter<RepositoryAdapter.ViewHolder, Repository>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.repository_row, parent, false)

        return ViewHolder(view)

    }

    override suspend fun loadPage(page: Int): List<User> = apiClient!!.listUsers(page).await()

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val user = items[position]
        holder.titleView.text = "${user.email}  (${user.role})"
        holder.apply { arrayOf(deleteView, clientView, realtorView, adminView).forEach { it.tag = user.id } }
        holder.apply { arrayOf(clientView, realtorView, adminView).forEach { it.visibility = View.VISIBLE } }
        when (user.role) {
            "admin" -> {
                holder.adminView.visibility = View.GONE
                holder.iconView.setColorFilter(Color.RED)
            }
            "realtor" -> {
                holder.realtorView.visibility = View.GONE
                holder.iconView.setColorFilter(Color.BLUE)
            }
            "client" -> {
                holder.clientView.visibility = View.GONE
                holder.iconView.setColorFilter(Color.BLACK)
            }
        }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val amountView: TextView = view.amount
        val priceView: TextView = view.price
        val changeView: TextView = view.change
    }

}


