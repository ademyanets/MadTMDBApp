package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.demyanets.andrey.mytmdbapp.ListingRouter
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.R
import com.demyanets.andrey.mytmdbapp.TmdbApplication
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.MoviesAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import java.lang.Exception
import java.util.concurrent.ThreadPoolExecutor

fun Fragment.stringResource(id: Int): String {
    return resources.getString(id)
}

class TopRatedCarouselFragment: CarouselFragment() {
    private val refreshModel: MainViewModel by activityViewModels()
    private val viewModel: TopRatedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        binding.itemTitle.text = stringResource(R.string.top_rated)
        binding.topRatedSpinner.visibility = View.VISIBLE

        refreshModel.reloadFlag.observe(viewLifecycleOwner) {
            viewModel.loadFirstPage()
            binding.topRatedSpinner.visibility = View.VISIBLE
            binding.errorPanel.visibility = View.GONE
        }

        viewModel.loadFirstPage()

        viewModel.items.observe(viewLifecycleOwner) {
            onReceiveData(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            onReceiveError(it)
        }

        MoviesAdapter.itemOnClick = ::onSelectItem
        binding.recyclerView.apply {
            adapter = MoviesAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.itemMore.setOnClickListener {
            moreButtonClick()
        }
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: Movie) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openDetails(movie)
    }

    private fun moreButtonClick() {
        Toast.makeText(activity, R.string.top_rated_hint, Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openTopRatedListing()
    }

    private fun onReceiveData(items: Array<Movie>) {
        binding.topRatedSpinner.visibility = View.GONE
        binding.errorPanel.visibility = View.GONE
        binding.itemMore.visibility = View.VISIBLE
        setTableData(items)
    }

    private fun onReceiveError(ex: Exception) {
        binding.topRatedSpinner.visibility = View.GONE
        binding.errorPanel.visibility = View.VISIBLE
        binding.itemMore.visibility = View.GONE
        setTableData(emptyArray(), false)
        Toast.makeText(activity, ex.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun setTableData(items: Array<Movie>, increment: Boolean = true) {
        binding.recyclerView.apply {
            (adapter as MoviesAdapter).let { carouselAdapter ->
                carouselAdapter.dataSet = if (increment) carouselAdapter.dataSet + items else items
                carouselAdapter.notifyDataSetChanged()
            }
        }
    }
}