package com.demyanets.andrey.mytmdbapp.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.demyanets.andrey.mytmdbapp.R
import com.demyanets.andrey.mytmdbapp.model.dto.ResultDTO

class TopRatedAdapter(private val dataSet: Array<ResultDTO>) :
    RecyclerView.Adapter<TopRatedAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleView: TextView
        val categoryView: TextView
        val textView: TextView

        init {
            titleView = view.findViewById(R.id.item_title)
            categoryView = view.findViewById(R.id.item_category)
            textView = view.findViewById(R.id.item_text)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.recycle_view_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val movie = dataSet[position]
        viewHolder.titleView.text = movie.title
        viewHolder.categoryView.text = movie.vote_average.toString()
        viewHolder.textView.text = movie.overview
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
