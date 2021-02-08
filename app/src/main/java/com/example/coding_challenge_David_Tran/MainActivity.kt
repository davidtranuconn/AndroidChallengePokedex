package com.example.coding_challenge_David_Tran

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PokedexAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var aboutMeButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerview)
        adapter = PokedexAdapter(applicationContext)
        layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        aboutMeButton = findViewById(R.id.about_me_button)
        aboutMeButton.setOnClickListener { openAboutMe() }
    }

    //inflate menu resource
    //https://developer.android.com/guide/topics/ui/menus
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem: MenuItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return true
    }

    //https://developer.android.com/reference/android/widget/SearchView.OnQueryTextListener
    override fun onQueryTextChange(newText: String): Boolean {
        adapter.filter.filter(newText)
        return false
    }

    override fun onQueryTextSubmit(newText: String): Boolean {
        adapter.filter.filter(newText)
        return false
    }

    private fun openAboutMe() {
        val intent = Intent(this, AboutMe::class.java)
        startActivity(intent)
    }
}