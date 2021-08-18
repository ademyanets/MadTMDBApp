package com.demyanets.andrey.mytmdbapp

import android.os.Handler
import android.util.Log
import com.demyanets.andrey.mytmdbapp.view.text
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.Executor
import javax.net.ssl.HttpsURLConnection

interface TmdbRepository {
    fun getTopRated(page: Int, cb: (res: RequestResult) -> Unit ): Unit
    fun getMovie(id: Int, cb: (res: RequestResult) -> Unit): Unit
}

class NetworkRepository(
        private val executor: Executor,
        private val resultHandler: Handler
    ): TmdbRepository {

    override fun getTopRated(page: Int, cb: (res: RequestResult) -> Unit) {
        executor.execute {
            try {
                val ret = makeGetTopRatedRequest(page)
                resultHandler.post { cb(RequestResult.ObjSuccess(ret)) }
            } catch (e: Exception) {
                Log.d("GGGG", "getTopRated exceoption", e)
                resultHandler.post { cb(RequestResult.Error(e)) }
            }
        }
    }

    override fun getMovie(id: Int, cb: (res: RequestResult) -> Unit): Unit {
        executor.execute {
            try {
                val ret = makeGetMovieRequest(id)
                resultHandler.post { cb(RequestResult.ObjSuccess(ret)) }
            } catch (e: Exception) {
                Log.d("GGGG", "getMovie exception", e)
                resultHandler.post { cb(RequestResult.Error(e)) }
            }
        }
    }

    private fun makeGetMovieRequest(id: Int): MovieDTO {
        val url =
            URL("https://api.themoviedb.org/3/movie/$id?api_key=5a6625fd71210561e22960146bc39db9")
        var conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 3000

        val data = BufferedReader(InputStreamReader(conn.inputStream)).text()
        val moovie = Gson().fromJson<MovieDTO>(data, MovieDTO::class.java)

        return moovie
    }

    private fun makeGetTopRatedRequest(page: Int = 0): PageResultDTO<ResultDTO> {
        val url =
            URL("https://api.themoviedb.org/3/movie/top_rated?api_key=5a6625fd71210561e22960146bc39db9")
        var conn: HttpsURLConnection = url.openConnection() as HttpsURLConnection
        conn.requestMethod = "GET"
        conn.connectTimeout = 3000

        //https://code.luasoftware.com/tutorials/android/gson-fromjson-list/
        val data = BufferedReader(InputStreamReader(conn.inputStream)).text()
        val type = object: TypeToken<PageResultDTO<ResultDTO>>() {}.type
        val page = Gson().fromJson<PageResultDTO<ResultDTO>>(data, type)

        return page
    }
}
