package com.hong.hongrxkotlin

/**
 * Created by Hong on 2017/12/21.
 * 订阅源（被观察者）
 * 分析整个程序的入口
 */
class Observable <T> constructor(val onSubscribe: OnSubscribe<T>){

    //---第一步：基础---

    companion object{
        /**
         * 返回一个带有OnSubscribe接口实例的Observable对象
         * */
        fun <T> create(onSubscribe: OnSubscribe<T>):Observable<T>{
            return Observable(onSubscribe)
        }
    }

    /**
     *订阅方法：实例化观察者并且可以向该观察者发送消息了
     * in T表示泛型中的消费者 相当于Java中的 ? super T(T以及T的父类)
     * out T表示泛型中的生产者 相当于Java中的 ? extends T(T以及T的子类)
     * */
    fun subscribe(subscriber: Subscriber<in T>){
        subscriber.onStart()
        onSubscribe.call(subscriber)
    }

    //订阅接口
    interface OnSubscribe <T>{
        /**
         * 把观察者传入被观察者中
         * */
        fun call(subscriber:Subscriber<in T>)
    }

    //---第二步：操作符---
    /*
    * 每次的操作符都会默认内部生成一个观察者
    * 在call方法里去订阅调用操作符的被观察者，
    * 并且返回一个新的被观察者以让下一个观察者可以订阅，
    * 这样整条逻辑链就被连接起来了
    */

    interface Transformer<T,R>{//把T类型转换成R类型的接口
        fun transform(t:T):R
    }

    fun <R> map(transformer: Transformer<in T, out R>):Observable<R>{
        return create(object :OnSubscribe<R>{
            override fun call(subscriber: Subscriber<in R>) {
              this@Observable.subscribe(object : Subscriber<T>(){//这行是操作符能全部连接起来的关键
                  override fun onComplete() {
                  }

                  override fun onError(t: Throwable) {
                  }

                  override fun onNext(t: T) {
                      subscriber.onNext(transformer.transform(t))
                  }

              })
            }

        })
    }

    /*下面思考如果把所有的操作符的逻辑都添加到Observable类中，那这个类一定会变得无比庞大。
    * 所以我们需要对Observable这个类的操作符相关功能进行解耦拆分,由此拆分出来两个类
    * MapOnSubscribe（代替OnSubscribe接口的功能） MapSubscriber（代替Map中默认生成的Subscriber对象）
    * */

    /**
     * 解耦后的Map方法
     * */
    fun <R> map_(transformer: Transformer<T, R>):Observable<R>{
        return create(MapOnSubscribe(this@Observable,transformer))
    }

    //---第三步：线程切换（线程调度的方法也是应该解耦的，为了方便分析这里就不写了）---

    /**
     * subscribeOn方法调度函数线程
     * */
    fun subscribeOn(scheduler: Scheduler):Observable<T>{
        return create(object : OnSubscribe<T>{
            override fun call(subscriber: Subscriber<in T>) {
                /*这里this@Observable.subscribe()方法的subscriber参数就是最后的观察者对象。调用的任何一个subscribeOn方法
                *都会把该对象往前（上）传，所以最终决定该对象到底在哪个线程执行的subscribeOn
                * 方法就是第一个，因此其它的subscribeOn方法无效
                * */
                scheduler.createWorker().schedule(Runnable {this@Observable.subscribe(subscriber)})
            }
        })
    }

    /**
     * observeOn方法调度函数线程
     * */
    fun observeOn(scheduler: Scheduler):Observable<T>{
        return create(object : OnSubscribe<T>{
            override fun call(subscriber: Subscriber<in T>) {
                val worker = scheduler.createWorker()
                /*注意这里this@Observable.subscribe的方法参数则是我们自己建立的Subscriber对象，
                实际的最后观察者会在这里被切换到不同的线程中操作。当然有最后决定权的observeOn
                就是最后一个observeOn方法。
                * */
                this@Observable.subscribe(object : Subscriber<T>(){
                    override fun onComplete() {
                        worker.schedule(java.lang.Runnable { subscriber.onComplete() })
                    }

                    override fun onError(t: Throwable) {
                        worker.schedule(java.lang.Runnable { subscriber.onError(t) })
                    }

                    override fun onNext(t: T) {
                        worker.schedule(java.lang.Runnable { subscriber.onNext(t) })
                    }

                })
            }
        })
    }


}