package com.demyanets.andrey.mytmdbapp

import android.app.Application
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

//TOOD: где хранить threadExecutor и как передавать?
class TmdbApplication: Application() {
    private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()
    private val workQueue: BlockingQueue<Runnable> = LinkedBlockingQueue<Runnable>()

    // Sets the amount of time an idle thread waits before terminating
    private val KEEP_ALIVE_TIME = 1L
    // Sets the Time Unit to seconds
    private val KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS
    // Creates a thread pool manager
    var threadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
        NUMBER_OF_CORES,       // Initial pool size
        NUMBER_OF_CORES,       // Max pool size
        KEEP_ALIVE_TIME,
        KEEP_ALIVE_TIME_UNIT,
        workQueue
    )
        private set
}