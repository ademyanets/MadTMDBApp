package com.demyanets.andrey.mytmdbapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.demyanets.andrey.mytmdbapp.R

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
            override fun onClick(v: View?) {
                label.text = "Button Pressed!"
                Toast.makeText(activity, "GGG", Toast.LENGTH_LONG).show()
            }
        })
    }
}