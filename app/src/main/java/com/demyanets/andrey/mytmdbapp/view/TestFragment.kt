package com.demyanets.andrey.mytmdbapp.view

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.demyanets.andrey.mytmdbapp.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class TestFragment: Fragment() {

    lateinit var button: Button
    lateinit var label: TextView

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
        button.setOnClickListener(object: View.OnClickListener {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onClick(v: View?) {
                Thread {
                    makeRequest(::requestCompletion)
                }.start()
            }
        })
    }

    private fun requestCompletion(data: String?): Unit {
        activity?.runOnUiThread {
            label.text = data
            Toast.makeText(activity, "GGG", Toast.LENGTH_LONG).show()
        }
    }

    private fun makeRequest(cb: (data: String?) -> Unit) {
        label.text = "Button Pressed!"

        var conn: HttpsURLConnection? = null
        try {
            val url =
                URL("https://api.themoviedb.org/3/movie/550?api_key=5a6625fd71210561e22960146bc39db9")
            conn = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 3000

            cb(BufferedReader(InputStreamReader(conn.inputStream)).text())
        } catch (e: MalformedURLException) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
        } finally {
            conn?.disconnect()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun BufferedReader.text(): String? {
    return this.lines().collect(Collectors.joining("\n"))
}