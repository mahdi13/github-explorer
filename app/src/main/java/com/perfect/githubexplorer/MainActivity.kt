package com.perfect.githubexplorer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders

class MainActivity : AppCompatActivity() {

    private lateinit var viewmodel: SearchViewModel
    private lateinit var adapter:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewmodel = ViewModelProviders.of(this).get(SearchViewModel::class.java)



        viewmodel.repositories.observe(this) {
            adapter.items = deals
            adapter.notifyDataSetChanged()
        }

    }
}
