package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demyanets.andrey.mytmdbapp.NetworkRepository
import com.demyanets.andrey.mytmdbapp.R
import com.demyanets.andrey.mytmdbapp.TmdbApplication
import com.demyanets.andrey.mytmdbapp.TmdbRepository
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
        repository.getTopRated(0, ::requestCompletion)
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

        TopRatedAdapter.Companion.itemOnClick = ::onSelectItem
        table = view.findViewById<RecyclerView>(R.id.recycler_view)
        table.apply {
            adapter = TopRatedAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun onSelectItem(movie: ResultDTO) {
        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
    }

    private fun requestCompletion(result: RequestResult) {
        when(result) {
            is RequestResult.EmptyResultSuccess -> TODO()
            is RequestResult.Error -> Toast.makeText(activity, result.e.toString(), Toast.LENGTH_LONG).show()
            is RequestResult.ObjSuccess<*> -> {
                val page = result.data as PageResultDTO<ResultDTO>//FIXME: is it optional??
                val items = page.results.toTypedArray()//FIXME: is it optional??
                table.apply {
                    adapter = TopRatedAdapter(items)
                }
                spinner.visibility = View.GONE
            }
        }
    }
}