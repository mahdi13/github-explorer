package com.perfect.githubexplorer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.perfect.githubexplorer.ui.ProfileAdapter
import com.perfect.githubexplorer.ui.ProfileViewModel
import com.perfect.githubexplorer.data.Repository
import com.perfect.githubexplorer.data.User
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.startActivity

class UserProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var adapter: ProfileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)

        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.user.observe(this, Observer {
            supportActionBar?.title = it?.username
        })

        viewModel.repositories.observe(this, Observer {
            initAdapter(viewModel.user.value!!)
        })

        viewModel.username.postValue(intent.getStringExtra(EXTRA_USERNAME))
    }

    private fun initAdapter(user: User) {
        adapter = ProfileAdapter(
            Glide.with(this),
            listOf(
                Pair(getString(R.string.username), user.username),
                Pair(getString(R.string.email), user.email ?: getString(R.string.not_available)),
                Pair(getString(R.string.company), user.company ?: getString(R.string.not_available)),
                Pair(getString(R.string.location), user.location ?: getString(R.string.not_available)),
                Pair(getString(R.string.bio), user.bio ?: getString(R.string.not_available)),
                Pair(getString(R.string.followers), user.followers?.toString() ?: getString(R.string.not_available))
            )
        )

        adapter.onRepositorySelected = {
            startActivity<RepositoryActivity>(
                RepositoryActivity.EXTRA_ID to it
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

    override fun onOptionsItemSelected(item: MenuItem) = finish().run { true }

    companion object {
        const val EXTRA_USERNAME = "username"
    }

}
