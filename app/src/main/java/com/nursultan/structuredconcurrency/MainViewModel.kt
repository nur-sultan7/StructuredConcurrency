package com.nursultan.structuredconcurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val coroutineScopeNoEH = CoroutineScope(
        Dispatchers.Default
    )

    fun start() {

        val childJob1 = coroutineScope.launch(Dispatchers.Default) {
            coroutineScopeWithException.launch(Dispatchers.Unconfined) {
                error()
            }
            delay(2000)
            Log.d(LOG_TAG, "childJob1 finished")
        }
        val childJob2 = coroutineScope.launch {
            delay(2500)
            Log.d(LOG_TAG, "childJob2 finished")
        }
        val childJob3 = viewModelScope.async {
            delay(3000)
            error()
            Log.d(LOG_TAG, "childJob3 finished")
        }
        val childJob4 = coroutineScope.launch {
            delay(3500)
            Log.d(LOG_TAG, "childJob4 finished")
        }
        coroutineScopeNoEH.launch {

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