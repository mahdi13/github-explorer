package com.perfect.githubexplorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import com.perfect.githubexplorer.adapter.RepositoryAdapter
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        adapter = RepositoryAdapter()
        viewModel.repositories.observe(
            this,
            Observer<PagedList<Repository>> { pagedList -> adapter.submitList(pagedList) }
        )
        list.adapter = adapter

    }
}
