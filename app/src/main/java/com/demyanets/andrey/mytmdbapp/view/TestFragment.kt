package com.demyanets.andrey.mytmdbapp.view

import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.demyanets.andrey.mytmdbapp.*
import com.demyanets.andrey.mytmdbapp.model.RequestResult
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import java.io.BufferedReader
import java.util.concurrent.ThreadPoolExecutor
import java.util.stream.Collectors

class TestFragment: Fragment() {

    lateinit var button: Button
    lateinit var label: TextView
    lateinit var input: EditText

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
        return inflater.inflate(R.layout.test_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.input = view.findViewById(R.id.test_input)
        this.input.setText(DEFAULT_ID.toString())
        this.button = view.findViewById(R.id.test_button)
        this.label = view.findViewById(R.id.test_title_text)
        button.setOnClickListener {
            val id = Integer.valueOf(input.text.toString())
            repository.getMovie(id, ::requestCompletion)
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