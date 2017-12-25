package com.hong.hongrxkotlin

/**
 * Created by Hong on 2017/12/22.
 * 线程调度器抽象类
 */
abstract class Scheduler{

    open fun createWorker():Worker{
        return Worker()
    }

    //RxJava中是一个静态内部类
    open inner class Worker{
        open fun schedule(runnable: Runnable){

        }
    }

}