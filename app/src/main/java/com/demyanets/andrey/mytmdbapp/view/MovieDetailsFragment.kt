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
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.databinding.DetailsFragmentBinding
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO
import com.demyanets.andrey.mytmdbapp.viewmodel.MovieDetailsViewModel
import com.demyanets.andrey.mytmdbapp.viewmodel.TopRatedViewModel
import java.io.BufferedReader
import java.util.concurrent.ThreadPoolExecutor
import java.util.stream.Collectors

class MovieDetailsFragment: Fragment() {

    private var _binding: DetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieDetailsViewModel by viewModels()
    private var DEFAULT_ID: Int = 550 //Fight Club
    private var movieId: Int = DEFAULT_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieId = arguments?.getInt("id") ?: DEFAULT_ID
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel?.let {
            val app: TmdbApplication = requireActivity().application as TmdbApplication//TODO: can throw!
            val tp: ThreadPoolExecutor = app.threadPoolExecutor
            val handler = Handler(Looper.getMainLooper())
            viewModel.setRepository(NetworkRepository(tp, handler))
            viewModel.getMovie(movieId)
        }

        viewModel.movie.observe(viewLifecycleOwner) { movie ->
            binding.movieDetailsTitle.setText(movie.title)
            binding.movieDetailsReview.setText("${movie.overview}\n\n${movie.homepage}")
            binding.movieDetails.setText("${movie.genres[0].name}\nOverall: ${movie.vote_average}\n\nReleased ${movie.release_date}\n\nBudget ${movie.budget}$")
            binding.movieDetailsImage.load("https://image.tmdb.org/t/p/original${movie.backdrop_path}") //FIXME: add /configuration request
            addLogos(movie)

            binding.movieDetailsSpinner.visibility = View.GONE
            Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(activity, it.toString(), Toast.LENGTH_LONG).show()
        }

    }

    //! Add logos programmatically via constraint layout flow helper
    private fun addLogos(movie: MovieDTO) {
        if (binding.flow.referencedIds.count() != 0) {
            return
        }

        var ids: Array<Int> = emptyArray()
        movie.production_companies.forEachIndexed { index, comp ->
            var logo = ImageView(activity)
            logo.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, 150)
            logo.load("https://image.tmdb.org/t/p/w500${comp.logo_path}")
            logo.id = comp.id
            ids += logo.id
            binding.movieDetailsLayout.addView(logo)
        }
        binding.flow.setReferencedIds(ids.toIntArray())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun BufferedReader.text(): String? {
    return this.lines().collect(Collectors.joining("\n"))
}