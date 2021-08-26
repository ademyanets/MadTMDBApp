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
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.TopRatedAdapter
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import java.util.concurrent.ThreadPoolExecutor

class TopRatedFragment: Fragment() {

    lateinit var table: RecyclerView
    lateinit var spinner: ProgressBar
    lateinit var refresh: SwipeRefreshLayout

    private val viewModel: TopRatedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_rated_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refresh = view.findViewById<SwipeRefreshLayout>(R.id.swipe_container)
        spinner = view.findViewById<ProgressBar>(R.id.top_rated_spinner)
        table = view.findViewById<RecyclerView>(R.id.recycler_view)

        setupControls()
        bindViewmodel()
    }

    private fun setupControls() {
        spinner.visibility = View.VISIBLE

        refresh.setOnRefreshListener {
            (table.adapter as TopRatedAdapter)?.let {
                Toast.makeText(activity, "Refreshing page...", Toast.LENGTH_SHORT).show()
                spinner.visibility = View.VISIBLE
                it.dataSet = emptyArray()
                it.notifyDataSetChanged()
                viewModel.loadFirstPage()
                refresh.isRefreshing = false
            }
        }

        TopRatedAdapter.Companion.itemOnClick = ::onSelectItem
        table.apply {
            adapter = TopRatedAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    adapter?.let {
                        if ((layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == it.itemCount - 1) {
                            Toast.makeText(activity, "Loading page...", Toast.LENGTH_SHORT).show()
                            spinner.visibility = View.VISIBLE
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
            table.apply {
                (adapter as TopRatedAdapter)?.let { topRatedAdapter ->
                    topRatedAdapter.dataSet += it
                    topRatedAdapter.notifyDataSetChanged()
                    spinner.visibility = View.GONE
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