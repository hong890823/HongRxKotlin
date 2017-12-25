package com.hong.hongrxkotlin

import android.os.Looper
import java.util.concurrent.Executors

/**
 * Created by Hong on 2017/12/22.
 * Scheduler的生产工厂类
 */
class Schedulers {
    companion object{
        val ioScheduler:Scheduler = ExecutorScheduler(Executors.newSingleThreadExecutor())
        val mainScheduler:Scheduler = LooperScheduler(Looper.getMainLooper())

        //子线程
        fun io():Scheduler{
            return ioScheduler
        }

        //主线程
        fun main():Scheduler{
            return mainScheduler
        }
    }

}