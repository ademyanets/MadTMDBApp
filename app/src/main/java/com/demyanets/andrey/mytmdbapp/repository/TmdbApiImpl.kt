package com.demyanets.andrey.mytmdbapp.repository

import android.util.Log
import com.demyanets.andrey.mytmdbapp.model.Movie
import com.demyanets.andrey.mytmdbapp.model.MovieDetails
import com.demyanets.andrey.mytmdbapp.model.RequestStatus
import com.demyanets.andrey.mytmdbapp.model.dto.MovieDTO
import com.demyanets.andrey.mytmdbapp.model.dto.convertToEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import javax.inject.Inject


//TODO: make service as dependency, not only retrofit instance
class TmdbApiImpl @Inject constructor(private val retrofit: Retrofit) : TmdbApi {
    override fun getMovie(id: Int, cb: (res: RequestStatus<MovieDTO>) -> Unit) {
        val service =
            retrofit.create(TmdbApiRetrofitService::class.java) //FIXME: // RetrofitClient.getClient().create(TmdbApiRetrofitService::class.java)

        service.getMoviewDetails(id).enqueue(object : Callback<MovieDTO> {
            override fun onFailure(call: Call<MovieDTO>, t: Throwable) {
                Log.d("GGG", t.toString())
                cb(RequestStatus.Error(Exception(t.localizedMessage)))//_movie.value = RequestStatus.Error(Exception(t.localizedMessage))
            }

            override fun onResponse(call: Call<MovieDTO>, response: Response<MovieDTO>) {
                response.body()?.let {
                    cb(RequestStatus.ObjSuccess(it))
//                    val value: MovieDetails? = it.convertToEntity()
//                    if (value != null) {
//                        _movie.value = RequestStatus.ObjSuccess(value!!)
//                    } else {
//                        Log.d("GGG", "MovieDetails convertion error")
//                        _movie.value =
//                            RequestStatus.Error(Exception("MovieDetails convertion error"))
//                    }
                }
            }
        })

    }
}