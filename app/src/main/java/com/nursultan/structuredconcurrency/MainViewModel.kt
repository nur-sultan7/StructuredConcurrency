package com.nursultan.structuredconcurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.lang.RuntimeException
import kotlin.concurrent.thread

class MainViewModel : ViewModel() {
    private val parentJob = SupervisorJob()
    private val coroutineEH = CoroutineExceptionHandler { _, throwable ->
        Log.d(LOG_TAG, "Exception is: $throwable")
    }
    private val coroutineScope = CoroutineScope(
        Dispatchers.Main
                + CoroutineName("Main Coroutine")
                + parentJob
                + coroutineEH
    )
    private val coroutineScopeWithException =
        CoroutineScope(Dispatchers.Main + coroutineEH)

    fun start() {
        val childJob1 = coroutineScope.launch {
            coroutineScopeWithException.launch {
                error()
            }
            delay(2000)
            Log.d(LOG_TAG, "childJob1 finished")
        }
        val childJob2 = coroutineScope.launch {
            delay(3000)
            Log.d(LOG_TAG, "childJob2 finished")
        }
        val childJob3 = coroutineScope.async {
            delay(2500)
            error()
        }
        coroutineScope.launch {
            childJob3.await()
        }
        thread {
            Thread.sleep(4500)
            Log.d(LOG_TAG, parentJob.isActive.toString())
        }
    }

    private fun error() {
        throw RuntimeException()
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }

    companion object {
        const val LOG_TAG = "MainViewModel"
    }
}