package com.hong.hongrxkotlin

import com.hong.hongrxkotlin.Observable.OnSubscribe

/**
 * Created by Hong on 2017/12/22.
 * 操作符解耦类
 *
 */
class  MapOnSubscribe<T,R> constructor(val observable: Observable<T>,val transformer: Observable.Transformer<in T,out R>): OnSubscribe<R>{

    override fun call(subscriber: Subscriber<in R>) {
        observable.subscribe(MapSubscriber(subscriber,transformer))
    }

    //这个类也可以分出去写，这里为了方便就写成内部类了
    class MapSubscriber <T,R> constructor(val subscriber: Subscriber<in R>,val transformer: Observable.Transformer<in T,out R>): Subscriber<T>() {
        override fun onComplete() {
            subscriber.onComplete()
        }

        override fun onError(t: Throwable) {
            subscriber.onError(t)
        }

        override fun onNext(t: T) {
            subscriber.onNext(transformer.transform(t))
        }
    }

}