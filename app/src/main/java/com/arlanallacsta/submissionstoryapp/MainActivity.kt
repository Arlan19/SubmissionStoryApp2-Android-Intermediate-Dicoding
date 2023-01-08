package com.arlanallacsta.submissionstoryapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.arlanallacsta.submissionstoryapp.adapter.LoadingStateAdapter
import com.arlanallacsta.submissionstoryapp.databinding.ActivityMainBinding
import com.arlanallacsta.submissionstoryapp.datastore.UserPreference
import com.arlanallacsta.submissionstoryapp.login.LoginActivity
import com.arlanallacsta.submissionstoryapp.main.MainViewModel
import com.arlanallacsta.submissionstoryapp.maps.MapsActivity
import com.arlanallacsta.submissionstoryapp.story.StoryActivity
import com.arlanallacsta.submissionstoryapp.story.StoryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPreference: UserPreference
    private val viewModel: MainViewModel by viewModels()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        storyAdapter = StoryAdapter()

        userPreference = UserPreference(this)
        userPreference.isLogin = false

        setPagingData()
        getData()


        binding.fbAddStory.setOnClickListener{
            val addStory = Intent(this, StoryActivity::class.java)
            startActivity(addStory)
        }

        binding.fbMaps.setOnClickListener {
            val maps = Intent(this, MapsActivity::class.java)
            startActivity(maps)
        }


    }

    private fun getData() {
        viewModel.getStories(userPreference.token).observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }

    private fun setPagingData() {
        binding.apply {
            rvStory.setHasFixedSize(true)
            rvStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvStory.adapter = storyAdapter.withLoadStateHeaderAndFooter(
                header = LoadingStateAdapter { storyAdapter.retry() },
                footer = LoadingStateAdapter { storyAdapter.retry() }
            )
            swRefresh.setOnRefreshListener {
                storyAdapter.refresh()
                swRefresh.isRefreshing = false
                rvStory.visibility = View.GONE
            }
            btnTry.setOnClickListener {
                storyAdapter.retry()
            }
        }

        storyAdapter.addLoadStateListener {
            binding.apply {
                pbMain.isVisible = it.source.refresh is LoadState.Loading
                rvStory.isVisible = it.source.refresh is LoadState.NotLoading
                tvError.isVisible = it.source.refresh is LoadState.Error
                btnTry.isVisible = it.source.refresh is LoadState.Error

                if (it.source.refresh is LoadState.NotLoading &&
                    it.append.endOfPaginationReached &&
                    storyAdapter.itemCount < 1
                ) {
                    tvNotFound.isVisible = true
                    rvStory.isVisible = false
                } else {
                    tvNotFound.isVisible = false
                    rvStory.isVisible = true
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_change_language ->{
                val changeLanguage = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(changeLanguage)
                return true
            }
            R.id.menu_logout ->{
                val logout = AlertDialog.Builder(this)
                logout.setTitle(resources.getString(R.string.logout))
                logout.setMessage(getString(R.string.logout_confirm))
                logout.setPositiveButton(getString(R.string.logout_accept)){ _,_ ->
                    userPreference.clear()
                    val logoutConfirm = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(logoutConfirm)
                    this@MainActivity.finish()
                    setMessage(this, getString(R.string.logout_status_accept))
                }
                logout.setNegativeButton(getString(R.string.logout_denied)){ _,_ ->
                    setMessage(this, getString(R.string.logout_status_denied))
                }
                logout.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }


}