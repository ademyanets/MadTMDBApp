package com.demyanets.andrey.mytmdbapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.default_menu, menu)
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