package com.demyanets.andrey.mytmdbapp.view

import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.databinding.DetailsFragmentBinding
import com.demyanets.andrey.mytmdbapp.model.MovieDetails
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.viewmodel.MovieDetailsViewModel
import java.io.BufferedReader
import java.lang.Exception
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
    ): View {
        _binding = DetailsFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getMovie(movieId)
        viewModel.data.observe(viewLifecycleOwner) { status ->
            when(status) {
                is RequestStatus.Error -> { exc: Exception ->
                    Toast.makeText(activity, exc.toString(), Toast.LENGTH_LONG).show()
                }
                is RequestStatus.Loading -> Log.d("GGG", "TODO: add loaders")
                is RequestStatus.ObjSuccess<*> -> { status
                    ((status as RequestStatus.ObjSuccess<MovieDetails>)?.data).let { movie ->
                        setText(movie)
                        addLogos(movie)
                        binding.movieDetailsSpinner.visibility = View.GONE
                        Toast.makeText(activity, movie.title, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setText(movie: MovieDetails) {
        binding.movieDetailsTitle.text = movie.title
        binding.movieDetailsReview.text = "${movie.overview}\n\n${movie.homepage}"
        binding.movieDetails.text = "${movie.genres[0].name}\nOverall: ${movie.voteAverage}\n\nReleased ${movie.releaseDate}"

        binding.movieDetailsImage.load(Common.posterOriginalWidthUrl(movie.backdropPath)) //FIXME: add /configuration request
    }

    //! Add logos programmatically via constraint layout flow helper
    private fun addLogos(movie: MovieDetails) {
        if (binding.flow.referencedIds.count() != 0) {
            return
        }

        var ids: Array<Int> = emptyArray()
        movie.productionCompanies.forEachIndexed { index, comp ->
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