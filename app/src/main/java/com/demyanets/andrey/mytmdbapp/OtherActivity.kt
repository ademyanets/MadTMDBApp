package com.demyanets.andrey.mytmdbapp

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.view.ListingFragment
import com.demyanets.andrey.mytmdbapp.view.MovieDetailsFragment
import com.demyanets.andrey.mytmdbapp.view.TopRatedFragment
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OtherActivity: AppCompatActivity(), Router {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setCustomAnimations(
                    R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
                )

                replace(R.id.fragment_container, getFragment())
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        supportFragmentManager.commit {
            replace(R.id.fragment_container, getFragment(intent))
        }
    }

    //! Router interface
    override fun openDetails(movie: Movie) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, createDetailsFragment(movie.id))
        }
    }

    override fun openListing(genre: Genre) {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, createListingFragment(genre))
        }
    }

    override fun openTopRatedListing() {
        supportFragmentManager.commit {
            replace(R.id.fragment_container, createTopRatedFragment())
        }
    }

    //! Other funs
    private fun getFragment(newIntent: Intent? = null): Fragment {

        val someIntent = newIntent ?: intent

        val movieId: Int = someIntent.getIntExtra(Common.MovieKey, -1)
        if (movieId != -1) {
            return createDetailsFragment(movieId)
        }

        val genre: Genre? = someIntent.getParcelableExtra<Genre>(Common.GenreKey)
        if (genre != null) {
            return createListingFragment(genre)
        }

        return  createTopRatedFragment()
    }

    private fun createListingFragment(genre: Genre): Fragment {
        val listingFragment = ListingFragment()
        val args = Bundle()
        args.putParcelable(Common.GenreKey, genre)
        listingFragment.arguments = args

        return listingFragment
    }

    private fun createTopRatedFragment(): Fragment {
        return TopRatedFragment()
    }

    private fun createDetailsFragment(movieId: Int): Fragment {
        val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
        val args = Bundle()
        args.putInt("id", movieId) //TODO: try parcelable
        detailsFragment.arguments = args

        return detailsFragment
    }
}