package com.demyanets.andrey.mytmdbapp.di

import com.demyanets.andrey.mytmdbapp.repository.RetrofitClient
import com.demyanets.andrey.mytmdbapp.repository.TmdbApi
import com.demyanets.andrey.mytmdbapp.repository.TmdbApiImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

//@Module
//@InstallIn(SingletonComponent::class) //FIXME:
//class AppModult {
//    @Provides
////  @ActivityScoped
//    fun getRetrofitClient(): Retrofit = RetrofitClient.getTmdbClient()
//}

@Module
@InstallIn(ViewModelComponent::class) //FIXME:
abstract class ActivityModule {
    companion object {
        @Provides
//        @ActivityScoped
        fun getRetrofitClient(): Retrofit = RetrofitClient.getTmdbClient()
    }

    @Binds
//  @ActivityScoped
    abstract fun getTmdbApi(impl: TmdbApiImpl): TmdbApi
}