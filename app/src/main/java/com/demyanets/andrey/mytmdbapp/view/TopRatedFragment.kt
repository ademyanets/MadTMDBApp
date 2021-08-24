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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.PageResultDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.view.adapters.TopRatedAdapter
import java.util.concurrent.ThreadPoolExecutor

class TopRatedFragment: Fragment() {

    lateinit var table: RecyclerView
    lateinit var spinner: ProgressBar

    lateinit var repository: TmdbRepository

    //FIXME: инстанцировать и передавать репу а не копипасть в каждый фрагмент
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app: TmdbApplication = requireActivity().application as TmdbApplication//TODO: can throw!
        val tp: ThreadPoolExecutor = app.threadPoolExecutor
        val handler = Handler(Looper.getMainLooper())
        repository = NetworkRepository(tp, handler)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.top_rated_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spinner = view.findViewById<ProgressBar>(R.id.top_rated_spinner)
        spinner.visibility = View.VISIBLE
        repository.getTopRated(::requestCompletion)

        TopRatedAdapter.Companion.itemOnClick = ::onSelectItem
        table = view.findViewById<RecyclerView>(R.id.recycler_view)
        table.apply {
            adapter = TopRatedAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity)

            addOnScrollListener(object: OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    adapter?.let {
                        if ((layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == it.itemCount - 1) {
                            Toast.makeText(activity, "Loading page #${repository.getCurrentPage()}", Toast.LENGTH_SHORT).show()
                            spinner.visibility = View.VISIBLE
                            repository.getTopRated(::requestCompletion)
                        }
                    }
                }
            })
        }
    }

    //! Callback. When user selects a movie from list
    private fun onSelectItem(movie: ResultDTO) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        (activity as MainActivity).switchToMovieDetailsFragment(movie)
    }

    //! Callback. When top rated list request completes
    private fun requestCompletion(result: RequestResult) {
        when(result) {
            is RequestResult.EmptyResultSuccess -> TODO()
            is RequestResult.Error -> Toast.makeText(activity, result.e.toString(), Toast.LENGTH_LONG).show()
            is RequestResult.ObjSuccess<*> -> {
                val page = result.data as PageResultDTO<ResultDTO>

                page?.let { page
                    val items = page.results.toTypedArray()
                    items?.let {
                        table.apply {
                            (adapter as TopRatedAdapter)?.let {
                                it.dataSet += items
                                it.notifyDataSetChanged()
                            }
                        }
                    }
                }
                spinner.visibility = View.GONE
            }
        }
    }
}