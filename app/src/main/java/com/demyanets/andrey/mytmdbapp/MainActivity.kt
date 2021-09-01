package com.demyanets.andrey.mytmdbapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.fragment.app.commit
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.MovieDetailsFragment
import com.demyanets.andrey.mytmdbapp.view.TopRatedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
                )
                replace(R.id.fragment_container, TopRatedFragment())
            }
        }

        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
Log.d("GGG", "GGG ${intent}")
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d("GGG", "GGG ${intent}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)
//        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
//        val searchView = searchItem?.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                searchView.clearFocus()
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                if (newText == null || newText.isEmpty()) {
//                  //?
//                } else {
//                   //?
//                }
//                return false
//            }
//        })

    // Get the SearchView and set the searchable configuration
    val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
    (menu?.findItem(R.id.action_search)?.actionView as SearchView).apply {
        // Assumes current activity is the searchable activity
        setSearchableInfo(searchManager.getSearchableInfo(componentName))
        setIconifiedByDefault(true) // Do not iconify the widget; expand it by default
    }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            Log.d("GGGG", "TODO: perform search")
        }
        return super.onOptionsItemSelected(item)
    }

    fun switchToMovieDetailsFragment(movie: ResultDTO) {
        val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
        val args = Bundle()
        args.putInt("id", movie.id) //TODO: try parcelable
        detailsFragment.arguments = args

        supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
            )
            replace(R.id.fragment_container, detailsFragment)
            addToBackStack(null)
        }
    }
}