package kr.yhs.traffic.module

import android.util.Log
import androidx.compose.runtime.*
import kotlinx.coroutines.*

class StopWatch {
    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var isActive = false

    var timeMillis by mutableStateOf(0L)
    private var lastTimestamp = 0L

    fun start() {
        if (isActive) return
        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            this@StopWatch.isActive = true
            while (this@StopWatch.isActive) {
                delay(1000L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
            }
        }
    }

    fun pause() {
        isActive = false
    }

    fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        isActive = false
    }
}