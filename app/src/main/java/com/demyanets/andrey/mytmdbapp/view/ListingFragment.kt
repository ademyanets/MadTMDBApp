package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.databinding.ListingFragmentBinding
import com.demyanets.andrey.mytmdbapp.model.Genre
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.MoviesAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.GenreListingViewModel
import java.lang.Exception

class ListingFragment: Fragment() {
    private var _binding: ListingFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GenreListingViewModel by viewModels()
    private var genre: Genre? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        genre = arguments?.getParcelable(Common.GenreKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListingFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupControls()
        bindViewmodel()
    }

    private fun setupControls() {
        binding.swipeContainer.setOnRefreshListener {
            (binding.recyclerView.adapter as MoviesAdapter).let {
                Toast.makeText(activity, R.string.refresh_hint, Toast.LENGTH_SHORT).show()
                binding.topRatedSpinner.visibility = View.VISIBLE
                it.dataSet = emptyList()
                it.notifyDataSetChanged()
                viewModel.loadFirstPage(genre!!.id)//FIXME:
                binding.swipeContainer.isRefreshing = false
            }
        }

        MoviesAdapter.Companion.itemOnClick = ::onSelectItem
        binding.recyclerView.apply {
            adapter = MoviesAdapter(emptyList())
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    adapter?.let {
                        if ((layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == it.itemCount - 1) {
                            Toast.makeText(activity, R.string.load_hint, Toast.LENGTH_SHORT).show()
                            binding.topRatedSpinner.visibility = View.VISIBLE
                            viewModel.loadNextPage(genre!!.id)//FIXME:
                        }
                    }
                }
            })
        }
    }

    private fun bindViewmodel() {
        genre?.let {
            viewModel.setGenreAndLoad(it.id)
        }

        viewModel.data.observe(viewLifecycleOwner) {
            when(it) {
                is RequestStatus.Error -> onReceiveError(it.e)
                is RequestStatus.Loading -> setLoadingState()
                is RequestStatus.ObjSuccess -> onReceiveData(it.data)
            }
        }
    }

    private fun onReceiveData(items: List<Movie>) {
        binding.recyclerView.apply {
            (adapter as MoviesAdapter)?.let { topRatedAdapter ->
                topRatedAdapter.dataSet += items
                topRatedAdapter.notifyDataSetChanged()
                binding.topRatedSpinner.visibility = View.GONE
            }
        }
    }

    private fun setLoadingState() {
        binding.topRatedSpinner.visibility = View.VISIBLE
    }

    private fun onReceiveError(exc: Exception) {
        Toast.makeText(activity, exc.toString(), Toast.LENGTH_LONG).show()
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: Movie) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as MainActivity).openDetails(movie)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}