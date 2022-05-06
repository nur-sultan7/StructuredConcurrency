package com.nursultan.structuredconcurrency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class MainViewModel2 : ViewModel() {
    fun method() {
        val job = viewModelScope.launch(Dispatchers.Default) {
            val startedTime = System.currentTimeMillis()
            var counter = 0
            for (i in 0 until 100_000_000) {
                for (j in 0 until 100) {
                    ensureActive()
                    counter++
                }
            }
            Log.d(MainViewModel.LOG_TAG, "finished in : ${System.currentTimeMillis() - startedTime}")
        }
        job.invokeOnCompletion {
            Log.d(MainViewModel.LOG_TAG, "Exception is: $it")
        }
        viewModelScope.launch {
            delay(3000)
            job.cancel()
        }
    }
}