package com.hong.hongrxkotlin

import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Created by Hong on 2017/12/24.
 * 自定义Looper线程调度器（主要为了能在Android主线程中操作逻辑）
 */
class LooperScheduler constructor(val looper: Looper): Scheduler(){

    override fun createWorker(): Worker {
        val handler = Handler(looper)
        return HandlerWorker(handler)
    }

    inner class HandlerWorker constructor(val handler:Handler):Worker(){
        override fun schedule(runnable: Runnable) {
            val msg = Message.obtain(handler,runnable)
            handler.sendMessage(msg)
        }
    }

}