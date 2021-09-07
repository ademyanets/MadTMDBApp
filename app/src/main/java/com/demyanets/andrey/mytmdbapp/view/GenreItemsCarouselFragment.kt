package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demyanets.andrey.mytmdbapp.Common
import com.demyanets.andrey.mytmdbapp.ListingRouter
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.TmdbApplication
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.MoviesAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.GenreListingViewModel
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel
import java.lang.Exception
import java.util.concurrent.ThreadPoolExecutor

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
        binding.itemTitle.text = genre?.name

        refreshModel.reloadFlag.observe(viewLifecycleOwner) {
            viewModel.loadFirstPage()
            binding.topRatedSpinner.visibility = View.VISIBLE
            binding.errorPanel.visibility = View.GONE
        }

        (activity?.application as TmdbApplication)?.let {
            val tp: ThreadPoolExecutor = it.threadPoolExecutor
            val handler = Handler(Looper.getMainLooper())
            genre?.let {
                viewModel.setRepositoryAndLoadFirstPage(NetworkRepository(tp, handler), it.id)
            }
        }

        genre?.let { genre ->
            binding.topRatedSpinner.visibility = View.VISIBLE
            viewModel.items.observe(viewLifecycleOwner) {
                onReceiveData(it)
            }
            viewModel.error.observe(viewLifecycleOwner) {
                onReceiveError(it)
            }

            MoviesAdapter.Companion.itemOnClick = ::onSelectItem
            binding.recyclerView.apply {
                adapter = MoviesAdapter(emptyArray())
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }

            binding.itemMore.setOnClickListener {
                moreButtonClick(genre)
            }
        }
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: ResultDTO) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openDetails(movie)
    }

    private fun moreButtonClick(genre: Genre) {
        Toast.makeText(activity, genre.name, Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openListing(genre)
    }

    private fun onReceiveData(items: Array<ResultDTO>) {
        binding.errorPanel.visibility = View.GONE
        binding.itemMore.visibility = View.VISIBLE
        binding.recyclerView.apply {
            (adapter as MoviesAdapter)?.let { carouselAdapter ->
                carouselAdapter.dataSet += items
                carouselAdapter.notifyDataSetChanged()
                binding.topRatedSpinner.visibility = View.GONE
            }
        }
    }

    private fun onReceiveError(ex: Exception) {
        binding.topRatedSpinner.visibility = View.GONE
        binding.errorPanel.visibility = View.VISIBLE
        binding.itemMore.visibility = View.GONE
        Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT)

        binding.recyclerView.apply {
            (adapter as MoviesAdapter)?.let { carouselAdapter ->
                carouselAdapter.dataSet = emptyArray()
                carouselAdapter.notifyDataSetChanged()
            }
        }
    }
}