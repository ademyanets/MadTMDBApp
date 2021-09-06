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
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.CarouselFragment
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel

//TODO: extract into sealed class whatever..
const val GenreAction: Int = 28
const val GenreDocumentary: Int = 99
const val GenreTvShows: Int = 10770
const val GenreKey: String = "genre-id"
const val MovieKey: String = "movie-id"

interface ListingRouter {
    fun openDetails(movie: ResultDTO)
    fun openListing(genre: Genre)
}

fun CarouselFragment.setGenre(genre: Genre): CarouselFragment {
    val actionsArgs = Bundle()
    actionsArgs.putParcelable(GenreKey, genre)
    arguments = actionsArgs
    return this
}

class MainActivity : AppCompatActivity(), ListingRouter {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {

                val actionsCarousel = CarouselFragment().setGenre(Genre(GenreAction, "Action"))
                replace(binding.fragmentContainer1.id, actionsCarousel)

                val documentaryCarousel = CarouselFragment().setGenre(Genre(GenreDocumentary, "Documentary"))
                replace(binding.fragmentContainer2.id, documentaryCarousel)

                val seriesCarousel = CarouselFragment().setGenre(Genre(GenreTvShows, "Tv shows"))
                replace(binding.fragmentContainer3.id, seriesCarousel)
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

    override fun openDetails(movie: ResultDTO) {
        val intent = Intent(this, OtherActivity::class.java).apply {
            putExtra(MovieKey, movie.id)
        }
        startActivity(intent)
    }

    override fun openListing(genre: Genre) {
        val intent = Intent(this, OtherActivity::class.java).apply {
            putExtra(GenreKey, genre)
        }
        startActivity(intent)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.action_search) {
//            Log.d("GGGG", "TODO: perform search")
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    fun switchToMovieDetailsFragment(movie: ResultDTO) {
//        val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
//        val args = Bundle()
//        args.putInt("id", movie.id) //TODO: try parcelable
//        detailsFragment.arguments = args
//
//        supportFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
//            )
//            replace(R.id.fragment_container, detailsFragment)
//            addToBackStack(null)
//        }
//    }

//    override fun openDetailsFragment(movie: ResultDTO) {
//        val detailsFragment: MovieDetailsFragment = MovieDetailsFragment()
//        val args = Bundle()
//        args.putInt("id", movie.id) //TODO: try parcelable
//        detailsFragment.arguments = args
//
//        supportFragmentManager.commit {
//            setCustomAnimations(
//                R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out
//            )
//            replace(R.id.fragment_container, detailsFragment)
//            addToBackStack(null)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

