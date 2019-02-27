package com.perfect.githubexplorer

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfect.githubexplorer.ui.RepositoryAdapter
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.SearchView
import com.bumptech.glide.Glide
import org.jetbrains.anko.startActivity
import android.util.DisplayMetrics
import com.perfect.githubexplorer.data.SearchViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: RepositoryAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        setSupportActionBar(toolbar)
        toolbar.title = title

        initAdapter()

    }

    private fun initAdapter() {
        adapter = RepositoryAdapter(Glide.with(this))

        adapter.onRepositorySelected = {
            startActivity<RepositoryActivity>(
                RepositoryActivity.EXTRA_ID to it
            )
        }

        adapter.onProfileSelected = {
            startActivity<UserProfileActivity>(
                UserProfileActivity.EXTRA_USERNAME to it
            )
        }

        list.adapter = adapter

        viewModel.repositories.observe(this, Observer<PagedList<Repository>> {
            adapter.submitList(it)
        })

        viewModel.loadingStatus.observe(this, Observer {
            adapter.setNetworkState(it)
        })

        list.layoutManager = LinearLayoutManager(this)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        setupSearchView(menu.findItem(R.id.search))
        return true
    }

    private fun setupSearchView(searchMenuItem: MenuItem) {
        searchView = searchMenuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_title)

        try {
            val autoCompleteTextViewID = resources.getIdentifier("android:id/search_src_text", null, null)
            val searchAutoCompleteTextView =
                searchView.findViewById<View>(autoCompleteTextViewID) as AutoCompleteTextView
            searchAutoCompleteTextView.threshold = 1
        } catch (e: Exception) {
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.query.value = query
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

        viewModel.query.observe(this, Observer {
            searchView.setQuery(it ?: "", false)
        })

        searchView.maxWidth = DisplayMetrics().apply { windowManager.defaultDisplay.getMetrics(this) }.widthPixels / 2
        searchView.isIconified = false
        if (viewModel.query.value == null) viewModel.query.value = "HelloWorld"

    }


}
