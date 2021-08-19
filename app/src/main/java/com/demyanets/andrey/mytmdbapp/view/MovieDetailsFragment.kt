package com.demyanets.andrey.mytmdbapp.view

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.CircleCropTransformation
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import java.io.BufferedReader
import java.util.concurrent.ThreadPoolExecutor
import java.util.stream.Collectors

class MovieDetailsFragment: Fragment() {

    lateinit var text: TextView
    lateinit var title: TextView
    lateinit var image: ImageView
    lateinit var spinner: ProgressBar

    lateinit var repository: TmdbRepository

    val DEFAULT_ID: Int = 550

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app: TmdbApplication = requireActivity().application as TmdbApplication//TODO: can throw!
        val tp: ThreadPoolExecutor = app.threadPoolExecutor
        val handler = Handler(Looper.getMainLooper())
        repository = NetworkRepository(tp, handler)

        val id = arguments?.getInt("id") ?: DEFAULT_ID
        (repository as NetworkRepository).getMovie(id, ::requestCompletion)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.details_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = view.findViewById<TextView>(R.id.movie_details_title)
        text = view.findViewById<TextView>(R.id.movie_details_review)
        image = view.findViewById<ImageView>(R.id.movie_details_image)
        spinner = view.findViewById<ProgressBar>(R.id.movie_details_spinner)
        spinner.visibility = View.VISIBLE

        var params = this.image.layoutParams
        val size = 2 * view.width / 3
        params.width = size
        params.height = size
        image.layoutParams = params
    }

    private fun requestCompletion(result: RequestResult) {
        when(result) {
            is RequestResult.EmptyResultSuccess -> TODO()
            is RequestResult.Error -> Toast.makeText(activity, result.e.toString(), Toast.LENGTH_LONG).show()
            is RequestResult.ObjSuccess<*> -> {
                val movie = result.data as MovieDTO //TODO: FIXME:
                title.setText(movie.title)
                text.setText(movie.overview)
                image.load("https://image.tmdb.org/t/p/original${movie.backdrop_path}") {//FIXME: add /configuration request
                    crossfade(true)
                    placeholder(R.drawable.ic_backstep_preview)
                    transformations(CircleCropTransformation())
                }

                spinner.visibility = View.GONE
                Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun BufferedReader.text(): String? {
    return this.lines().collect(Collectors.joining("\n"))
}