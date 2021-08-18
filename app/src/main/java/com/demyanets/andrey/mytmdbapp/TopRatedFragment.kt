package com.demyanets.andrey.mytmdbapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.ThreadPoolExecutor

class TopRatedFragment: Fragment() {

    lateinit var table: RecyclerView

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
        table = view.findViewById<RecyclerView>(R.id.recycler_view)
        table.apply {
            adapter = TopRatedAdapter(emptyArray())
            layoutManager = LinearLayoutManager(activity)
        }
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
            }
        }
    }
}