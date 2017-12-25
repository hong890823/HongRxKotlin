package com.hong.hongrxkotlin

/**
 * Created by Hong on 2017/12/22.
 * 观察者
 */
interface Observer <T>{
    fun onComplete()
    fun onError(t:Throwable)
    fun onNext(t:T)
}