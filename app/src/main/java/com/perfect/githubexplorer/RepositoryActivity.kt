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
import com.perfect.githubexplorer.ui.RepositoryViewModel
import kotlinx.android.synthetic.main.activity_repository.*
import org.jetbrains.anko.startActivity

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
                "https://raw.githubusercontent.com/${repository?.fullName}/${repository?.defaultBranch}/README.md"
            )

            val naText = "Not Available"

            name.text = repository?.name ?: naText
            owner.text = repository?.owner?.username ?: naText
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
                    "username" to repository?.owner?.username
                )
            }

            email.text = repository?.owner?.email ?: naText
            default_branch.text = repository?.defaultBranch ?: naText
            forks.text = repository?.forks?.toString() ?: naText
            language.text = repository?.language ?: naText

        })

        viewModel.repositoryId.postValue(intent.getIntExtra("id", 0))

    }

    override fun onPause() = super.onPause().apply { glide.clear(imageLoaderTarget) }

    override fun onOptionsItemSelected(item: MenuItem) = finish().run { true }

}
