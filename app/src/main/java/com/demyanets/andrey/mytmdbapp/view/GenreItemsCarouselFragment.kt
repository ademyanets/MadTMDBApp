package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demyanets.andrey.mytmdbapp.Common
import com.demyanets.andrey.mytmdbapp.Router
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.view.adapters.MoviesAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.GenreListingViewModel
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel
import java.lang.Exception

class GenreItemsCarouselFragment: CarouselFragment() {
    private val refreshModel: MainViewModel by activityViewModels()
    private val viewModel: GenreListingViewModel by viewModels()
    var genre: Genre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = arguments?.getParcelable(Common.GenreKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        genre?.let { genre ->
            binding.itemTitle.text = genre.name

            refreshModel.reloadFlag.observe(viewLifecycleOwner) {
                viewModel.loadFirstPage(genre.id)
            }

            viewModel.setGenreAndLoad(genre.id)
            viewModel.data.observe(viewLifecycleOwner) {
                when(it) {
                    is RequestStatus.Error -> onReceiveError(it.e)
                    is RequestStatus.Loading -> setLoadingState()
                    is RequestStatus.ObjSuccess<List<Movie>> -> {
                        onReceiveData(it.data)
                    }
                }
            }

            MoviesAdapter.Companion.itemOnClick = ::onSelectItem

            binding.recyclerView.apply {
                adapter = MoviesAdapter(emptyList())
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }

            binding.itemMore.setOnClickListener {
                moreButtonClick(genre)
            }
        }
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: Movie) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as? Router)?.openDetails(movie)
    }

    private fun moreButtonClick(genre: Genre) {
        Toast.makeText(activity, genre.name, Toast.LENGTH_LONG).show()
        (activity as? Router)?.openListing(genre)
    }

    private fun setLoadingState() {
        binding.topRatedSpinner.visibility = View.VISIBLE
        binding.errorPanel.visibility = View.GONE
        binding.itemMore.visibility = View.GONE
        setTableData(emptyList())
    }

    private fun onReceiveData(items: List<Movie>) {
        binding.errorPanel.visibility = View.GONE
        binding.itemMore.visibility = View.VISIBLE
        binding.topRatedSpinner.visibility = View.GONE
        setTableData(items)
    }

    private fun onReceiveError(ex: Exception) {
        binding.topRatedSpinner.visibility = View.GONE
        binding.errorPanel.visibility = View.VISIBLE
        binding.itemMore.visibility = View.GONE
        setTableData(emptyList(), false)
        Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun setTableData(items: List<Movie>, increment: Boolean = true) {
        binding.recyclerView.apply {
            (adapter as MoviesAdapter).let { carouselAdapter ->
                carouselAdapter.dataSet = if (increment) carouselAdapter.dataSet + items else items
                carouselAdapter.notifyDataSetChanged()
            }
        }
    }
}