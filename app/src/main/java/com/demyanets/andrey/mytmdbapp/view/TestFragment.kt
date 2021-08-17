package com.demyanets.andrey.mytmdbapp.view

import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.demyanets.andrey.mytmdbapp.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.concurrent.ThreadPoolExecutor
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class TestFragment: Fragment() {

    lateinit var button: Button
    lateinit var label: TextView
    lateinit var repository: TmdbRepository

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
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.button = view.findViewById(R.id.test_button)
        this.label = view.findViewById(R.id.test_title_text)
        button.setOnClickListener {
            repository.getMovie(550, ::requestCompletion)
        }
    }

    private fun requestCompletion(result: RequestResult) {
        when(result) {
            is RequestResult.EmptyResultSuccess -> TODO()
            is RequestResult.Error -> Toast.makeText(activity, result.e.toString(), Toast.LENGTH_LONG).show()
            is RequestResult.ObjSuccess<*> -> {
                val movie = result.data as MovieDTO //TODO: FIXME:
                label.text = "${movie.title}: ${movie.overview}"
                Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun BufferedReader.text(): String? {
    return this.lines().collect(Collectors.joining("\n"))
}