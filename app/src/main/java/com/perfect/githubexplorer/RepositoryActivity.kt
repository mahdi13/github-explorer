package com.perfect.githubexplorer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.perfect.githubexplorer.data.GITHUB_MARKDOWN_URL
import com.perfect.githubexplorer.data.RepositoryViewModel
import kotlinx.android.synthetic.main.activity_repository.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class RepositoryActivity : AppCompatActivity() {

    private lateinit var viewModel: RepositoryViewModel
    private lateinit var glide: RequestManager
    private var imageLoaderTarget: Target<Drawable>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)

        setSupportActionBar(detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        glide = Glide.with(this)

        viewModel.repository.observe(this, Observer { repository ->
            supportActionBar?.title = repository?.name
            markdown_view.loadFromUrl(
                GITHUB_MARKDOWN_URL.format(repository?.fullName, repository?.defaultBranch)
            )

            name.text = repository?.name ?: getString(R.string.not_available)
            owner.text = repository?.owner?.username ?: getString(R.string.not_available)
            imageLoaderTarget = glide.load(repository?.owner?.avatarUrl)
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                    ) {
                        owner.chipIcon = resource
                    }

                })
            owner.setOnClickListener {
                startActivity<UserProfileActivity>(
                    UserProfileActivity.EXTRA_USERNAME to repository?.owner?.username
                )
            }

            email.text = repository?.owner?.email ?: getString(R.string.not_available)
            default_branch.text = repository?.defaultBranch ?: getString(R.string.not_available)
            forks.text = repository?.forks?.toString() ?: getString(R.string.not_available)
            language.text = repository?.language ?: getString(R.string.not_available)

        })

        viewModel.repositoryId.postValue(intent.getIntExtra(EXTRA_ID, 0))

    }

    override fun onPause() = super.onPause().apply { glide.clear(imageLoaderTarget) }

    override fun onOptionsItemSelected(item: MenuItem) = finish().run { true }


    companion object {
        const val EXTRA_ID = "id"
    }

}
