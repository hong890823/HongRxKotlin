package com.hong.hongrxkotlin

/**
 * Created by Hong on 2017/12/21.
 * 订阅者（观察者）
 */
abstract class Subscriber<T>:Observer<T>{
    fun onStart() {}
}