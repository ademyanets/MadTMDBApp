package com.demyanets.andrey.mytmdbapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.MovieDetailsFragment
import com.demyanets.andrey.mytmdbapp.view.TopRatedFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, TopRatedFragment())
                .commit()
        }
    }

    fun switchToMovieDetailsFragment(movie: ResultDTO) {
        val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
        val args = Bundle()
        args.putInt("id", movie.id) //TODO: try parcelable
        detailsFragment.arguments = args

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, detailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun switchToTopRatedFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, TopRatedFragment())
            .addToBackStack(null)
            .commit()
    }
}