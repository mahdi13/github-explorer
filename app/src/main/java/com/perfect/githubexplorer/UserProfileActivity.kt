package com.perfect.githubexplorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.perfect.githubexplorer.adapter.ProfileAdapter
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

        viewModel.username.postValue(intent.getStringExtra("username"))
    }

    private fun initAdapter(user: User) {
        adapter = ProfileAdapter(user) {
            //            viewModel.retry()
        }

        adapter.onRepositorySelected = {
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
    }


}
