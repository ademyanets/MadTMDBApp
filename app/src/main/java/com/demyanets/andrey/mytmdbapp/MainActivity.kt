package com.demyanets.andrey.mytmdbapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.demyanets.andrey.mytmdbapp.databinding.ActivityMainBinding
import com.demyanets.andrey.mytmdbapp.databinding.TopRatedFragmentBinding
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.CarouselFragment
import com.demyanets.andrey.mytmdbapp.view.GenreItemsCarouselFragment
import com.demyanets.andrey.mytmdbapp.view.TopRatedCarouselFragment
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

//TODO: extract into sealed class whatever..


interface Router {
    fun openDetails(movie: Movie)
    fun openListing(genre: Genre)
    fun openTopRatedListing()
}

fun GenreItemsCarouselFragment.setGenre(genre: Genre): CarouselFragment {
    val actionsArgs = Bundle()
    actionsArgs.putParcelable(Common.GenreKey, genre)
    arguments = actionsArgs
    return this
}

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), Router {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                val topRatedCarousel = TopRatedCarouselFragment()
                replace(binding.fragmentContainer1.id, topRatedCarousel)

                val actionsCarousel = GenreItemsCarouselFragment().setGenre(Genre(Genre.GenreAction, "Action"))
                replace(binding.fragmentContainer2.id, actionsCarousel)

                val documentaryCarousel = GenreItemsCarouselFragment().setGenre(Genre(Genre.GenreDocumentary, "Documentary"))
                replace(binding.fragmentContainer3.id, documentaryCarousel)
            }
        }

        binding.swipeContainer.setOnRefreshListener {
            viewModel.reload()
            binding.swipeContainer.isRefreshing = false
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

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.action_search)?.actionView as SearchView).apply {
            // Assumes current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(true) // Do not iconify the widget; expand it by default
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //! Router interface impl
    override fun openDetails(movie: Movie) {
        val intent = Intent(this, OtherActivity::class.java).apply {
            putExtra(Common.MovieKey, movie.id)
        }
        startActivity(intent)
    }

    override fun openListing(genre: Genre) {
        val intent = Intent(this, OtherActivity::class.java).apply {
            putExtra(Common.GenreKey, genre)
        }
        startActivity(intent)
    }

    override fun openTopRatedListing() {
        val intent = Intent(this, OtherActivity::class.java).apply {
            putExtra(Common.TopRatedKey, true)
        }
        startActivity(intent)
    }
}

