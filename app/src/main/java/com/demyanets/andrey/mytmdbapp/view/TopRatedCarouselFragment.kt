package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.demyanets.andrey.mytmdbapp.ListingRouter
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.TmdbApplication
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.MoviesAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.MainViewModel
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import java.lang.Exception
import java.util.concurrent.ThreadPoolExecutor

class TopRatedCarouselFragment: CarouselFragment() {
    private val refreshModel: MainViewModel by activityViewModels()
    private val viewModel: TopRatedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViewModel()
    }

    private fun bindViewModel() {
        binding.itemTitle.text = "Top Rated"

        refreshModel.reloadFlag.observe(viewLifecycleOwner) {
            viewModel.loadFirstPage()
            binding.topRatedSpinner.visibility = View.VISIBLE
            binding.errorPanel.visibility = View.GONE
        }

        (activity?.application as TmdbApplication)?.let {
            val tp: ThreadPoolExecutor = it.threadPoolExecutor
            val handler = Handler(Looper.getMainLooper())
            viewModel.setRepositoryAndLoadFirstPage(NetworkRepository(tp, handler))
        }


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
                moreButtonClick()
            }

    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: ResultDTO) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openDetails(movie)
    }

    private fun moreButtonClick() {
        Toast.makeText(activity, "Go to Top Rated List", Toast.LENGTH_LONG).show()
        (activity as ListingRouter).openTopRatedListing()
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
        Toast.makeText(activity, "Error ${ex}", Toast.LENGTH_SHORT)

        binding.recyclerView.apply {
            (adapter as MoviesAdapter)?.let { carouselAdapter ->
                carouselAdapter.dataSet = emptyArray()
                carouselAdapter.notifyDataSetChanged()
            }
        }
    }
}