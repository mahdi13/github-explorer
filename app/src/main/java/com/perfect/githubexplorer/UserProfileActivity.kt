package com.perfect.githubexplorer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.perfect.githubexplorer.ui.ProfileAdapter
import com.perfect.githubexplorer.data.ProfileViewModel
import com.perfect.githubexplorer.data.Repository
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.repository_row.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

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
            toolbar_layout.title = it?.username
            Glide.with(this).load(it?.avatarUrl)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        toolbar_view.setImageDrawable(resource)
                    }

                })
        })

        viewModel.repositories.observe(this, Observer {
            initAdapter()
        })

        viewModel.username.postValue(intent.getStringExtra(EXTRA_USERNAME))
    }

    private fun initAdapter() {
        adapter = ProfileAdapter(Glide.with(this), viewModel.user.value, this)

        adapter.onRepositorySelected = {
            startActivity<RepositoryActivity>(
                RepositoryActivity.EXTRA_ID to it
            )
        }

        list.adapter = adapter

        viewModel.repositories.observe(this, Observer<PagedList<Any>> {
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
