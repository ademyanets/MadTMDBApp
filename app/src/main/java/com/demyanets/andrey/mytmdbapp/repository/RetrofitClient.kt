package com.demyanets.andrey.mytmdbapp.repository

import com.demyanets.andrey.mytmdbapp.Common
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.Retrofit.*
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    const val tmdbUrl = "https://api.themoviedb.org/3/"
    const val tmdbApiKey = "5a6625fd71210561e22960146bc39db9"
    const val tmdbAuthorizationBearer = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1YTY2MjVmZDcxMjEwNTYxZTIyOTYwMTQ2YmMzOWRiOSIsInN1YiI6IjYxMTEyYjMxNGE1MmY4MDA1Y2M3NzBmYyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.1JrkvhI5zPB-yf6m3cKk6M65xx_UHCVbWTBuJWsY3PA"

    private var retrofit: Retrofit? = null

    fun getTmdbClient(): Retrofit {
        if (retrofit == null) {
            val client = OkHttpClient
                .Builder()
                .addInterceptor(object: Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "Bearer ${tmdbAuthorizationBearer}")
                            .addHeader("Content-Type", "application/json;charset=utf-8")
                            .build()
                        return chain.proceed(request)
                    }
                })
                .build()

            retrofit = Builder()
                .client(client)
                .baseUrl(tmdbUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        return retrofit!!
    }
}
