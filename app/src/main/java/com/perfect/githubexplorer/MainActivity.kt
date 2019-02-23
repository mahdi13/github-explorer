package com.perfect.githubexplorer

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfect.githubexplorer.adapter.RepositoryAdapter
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import org.jetbrains.anko.startActivity


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        setSupportActionBar(toolbar)
        toolbar.title = title

        initAdapter()

    }

    private fun initAdapter() {
        adapter = RepositoryAdapter(Glide.with(this)) {
            //            viewModel.retry()
        }

        adapter.onRepositorySelected = {
            startActivity<UserProfileActivity>(
                "username" to it
            )
        }

        adapter.onProfileSelected = {
            startActivity<UserProfileActivity>(
                "username" to it
            )
        }

        list.adapter = adapter
        viewModel.repositories.observe(this, Observer<PagedList<Repository>> {
            adapter.submitList(it)
        })
        viewModel.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
        list.layoutManager = LinearLayoutManager(this)

        viewModel.query.value = ""
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        setupSearchView(menu.findItem(R.id.search))
        return true
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        val searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_title) // your hint here

        try {
            val autoCompleteTextViewID = resources.getIdentifier("android:id/search_src_text", null, null)
            val searchAutoCompleteTextView =
                searchView.findViewById<View>(autoCompleteTextViewID) as AutoCompleteTextView
            searchAutoCompleteTextView.threshold = 1
        } catch (e: Exception) {
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.query.postValue(newText)
                return true
            }

        })

    }


}
