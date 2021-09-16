package com.demyanets.andrey.mytmdbapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class TmdbApplication: Application()
