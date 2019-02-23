package com.perfect.githubexplorer

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_repository.*

class RepositoryActivity : AppCompatActivity() {

    private lateinit var viewModel: RepositoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)

        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.repository.observe(this, Observer {
            supportActionBar?.title = it?.id.toString()
        })

        viewModel.repositoryId.postValue(intent.getIntExtra("id", 0))

    }

    override fun onOptionsItemSelected(item: MenuItem) = finish().run { true }

}
