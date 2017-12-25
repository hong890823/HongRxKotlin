package com.hong.hongrxkotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast

/*
* 该项目主要用于理解RxJava的核心原理，简单的完成RxJava的基础功能
* */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
   }

    fun sendMsg(v: View){
        Observable.create(object : Observable.OnSubscribe<Int>{//记住这种匿名内部类写法
            override fun call(subscriber: Subscriber<in Int>) {
                subscriber.onNext(123456)
            }
        }).subscribeOn(Schedulers.io()).map_(object : Observable.Transformer<Int,String>{
            override fun transform(t: Int): String {
                return "发射消息"+t
            }
        }).observeOn(Schedulers.main()).subscribe(object : Subscriber<String>(){
            override fun onComplete() {
            }

            override fun onError(t: Throwable) {
            }

            override fun onNext(t: String) {
                Toast.makeText(this@MainActivity,t,Toast.LENGTH_SHORT).show()
            }
        })
    }

}
