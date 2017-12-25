package com.hong.hongrxkotlin

import java.util.concurrent.Executor

/**
 * Created by Hong on 2017/12/24.
 * 自定义的线程池线程调度器
 */
class ExecutorScheduler constructor(val executor: Executor): Scheduler() {

    override fun createWorker(): Worker {
        return IOWorker(executor)
    }

    inner class IOWorker constructor(val executor: Executor):Worker(){
        override fun schedule(runnable: Runnable) {
            executor.execute(runnable)
        }
    }

}