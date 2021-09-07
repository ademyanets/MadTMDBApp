package com.demyanets.andrey.mytmdbapp.model

import android.os.Parcel
import android.os.Parcelable
import com.demyanets.andrey.mytmdbapp.model.dto.GenreDTO

class Genre(
    val id: Int,
    val name: String
) : Parcelable {

    constructor(dto: GenreDTO) : this(dto.id, dto.name)
    private constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readString().toString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Genre> {
        const val GenreKey: String = "genre-id"//FIXME:
        const val MovieKey: String = "movie-id"//FIXME:
        const val TopRatedKey: String = "top-rated"//FIXME:

        const val GenreAction: Int = 28
        const val GenreDocumentary: Int = 99
        const val GenreTvShows: Int = 10770

        override fun createFromParcel(parcel: Parcel): Genre {
            return Genre(parcel)
        }

        override fun newArray(size: Int): Array<Genre?> {
            return arrayOfNulls(size)
        }
    }


}