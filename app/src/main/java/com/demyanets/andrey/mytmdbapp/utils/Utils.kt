package com.demyanets.andrey.mytmdbapp.utils

import androidx.fragment.app.Fragment

fun Fragment.stringResource(id: Int): String {
    return resources.getString(id)
}