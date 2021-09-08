package com.demyanets.andrey.mytmdbapp

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.view.ListingFragment
import com.demyanets.andrey.mytmdbapp.view.MovieDetailsFragment
import com.demyanets.andrey.mytmdbapp.view.TopRatedFragment
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel

class OtherActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
                )

                val movieId: Int = intent.getIntExtra(Common.MovieKey, -1)


                if (movieId != -1) {
                    val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
                    val args = Bundle()
                    args.putInt("id", movieId) //TODO: try parcelable
                    detailsFragment.arguments = args
                    replace(R.id.fragment_container, detailsFragment)
                } else {
                    val genre: Genre? = intent.getParcelableExtra<Genre>(Common.GenreKey)
                    if (genre != null) {
                        val listingFragment = ListingFragment()
                        val args = Bundle()
                        args.putParcelable(Common.GenreKey, genre)
                        listingFragment.arguments = args
                        replace(R.id.fragment_container, listingFragment)
                    } else {
                        replace(R.id.fragment_container, TopRatedFragment())
                    }
                }
            }
        }
    }
}