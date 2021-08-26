package com.demyanets.andrey.mytmdbapp.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.databinding.TopRatedFragmentBinding
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.TopRatedAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import java.util.concurrent.ThreadPoolExecutor

class TopRatedFragment: Fragment() {
    private lateinit var binding: TopRatedFragmentBinding
    private val viewModel: TopRatedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TopRatedFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupControls()
        bindViewmodel()
    }

    private fun setupControls() {
        binding.topRatedSpinner.visibility = View.VISIBLE

        binding.swipeContainer.setOnRefreshListener {
            (binding.recyclerView.adapter as TopRatedAdapter)?.let {
                Toast.makeText(activity, "Refreshing page...", Toast.LENGTH_SHORT).show()
                binding.topRatedSpinner.visibility = View.VISIBLE
                it.dataSet = emptyArray()
                it.notifyDataSetChanged()
                viewModel.loadFirstPage()
                binding.swipeContainer.isRefreshing = false
            }
        }

        TopRatedAdapter.Companion.itemOnClick = ::onSelectItem
        binding.recyclerView.apply {
            adapter = TopRatedAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    adapter?.let {
                        if ((layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == it.itemCount - 1) {
                            Toast.makeText(activity, "Loading page...", Toast.LENGTH_SHORT).show()
                            binding.topRatedSpinner.visibility = View.VISIBLE
                            viewModel.loadNextPage()
                        }
                    }
                }
            })
        }
    }

    private fun bindViewmodel() {
        val app: TmdbApplication = requireActivity().application as TmdbApplication //FIXME:
        app?.let {
            val tp: ThreadPoolExecutor = app.threadPoolExecutor
            val handler = Handler(Looper.getMainLooper())
            viewModel.setRepositoryAndLoadFirstPage(NetworkRepository(tp, handler))
        }

        viewModel.items.observe(viewLifecycleOwner) {
            binding.recyclerView.apply {
                (adapter as TopRatedAdapter)?.let { topRatedAdapter ->
                    topRatedAdapter.dataSet += it
                    topRatedAdapter.notifyDataSetChanged()
                    binding.topRatedSpinner.visibility = View.GONE
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(activity, "Request error ${it}", Toast.LENGTH_LONG).show()
        }
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: ResultDTO) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as MainActivity).switchToMovieDetailsFragment(movie)
    }
}