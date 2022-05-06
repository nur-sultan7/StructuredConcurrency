package com.nursultan.structuredconcurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {
    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(
        Dispatchers.Main + CoroutineName("Main Coroutine") + parentJob
    )

    fun start() {
        val childJob1 = coroutineScope.launch {
            delay(1000)
            Log.d(LOG_TAG, "childJob1 finished")
        }
        val childJob2 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "childJob2 finished")
        }
        thread {
            Thread.sleep(4500)
            Log.d(LOG_TAG, parentJob.isActive.toString())
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        const val LOG_TAG = "MainViewModel"
    }
}