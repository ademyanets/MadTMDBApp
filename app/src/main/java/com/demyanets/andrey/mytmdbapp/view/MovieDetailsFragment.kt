package com.demyanets.andrey.mytmdbapp.view

import android.app.ActionBar
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
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
//    lateinit var rating: RatingBar
    lateinit var details: TextView
    lateinit var spinner: ProgressBar
    lateinit var companiesFlow: Flow
    lateinit var layout: ConstraintLayout

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

        layout = view.findViewById<ConstraintLayout>(R.id.movie_details_layout)
        title = view.findViewById<TextView>(R.id.movie_details_title)
        text = view.findViewById<TextView>(R.id.movie_details_review)
        details = view.findViewById<TextView>(R.id.movie_details)
        image = view.findViewById<ImageView>(R.id.movie_details_image)
        spinner = view.findViewById<ProgressBar>(R.id.movie_details_spinner)
        companiesFlow = view.findViewById<Flow>(R.id.flow)
//        rating = view.findViewById<RatingBar>(R.id.movie_details_rating)
        spinner.visibility = View.VISIBLE
    }

    private fun requestCompletion(result: RequestResult) {
        when(result) {
            is RequestResult.EmptyResultSuccess -> TODO()
            is RequestResult.Error -> Toast.makeText(activity, result.e.toString(), Toast.LENGTH_LONG).show()
            is RequestResult.ObjSuccess<*> -> {
                val movie = result.data as MovieDTO //TODO: FIXME:
                title.setText(movie.title)
                text.setText("${movie.overview}\n\n${movie.homepage}")
                details.setText("${movie.genres[0].name}\nOverall: ${movie.vote_average}\n\nReleased ${movie.release_date}\n\nBudget ${movie.budget}$")
                image.load("https://image.tmdb.org/t/p/original${movie.backdrop_path}") //FIXME: add /configuration request

                var ids: Array<Int> = emptyArray()
                movie.production_companies.forEachIndexed { index, comp ->
                    var logo = ImageView(activity)
                    logo.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 150)//(ConstraintLayout.LayoutParams.WRAP_CONTENT,100)
                    logo.load("https://image.tmdb.org/t/p/w500${comp.logo_path}")
                    logo.id = comp.id //FIXME:
                    ids += logo.id
                    layout.addView(logo)
                }
                companiesFlow.setReferencedIds(ids.toIntArray())

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