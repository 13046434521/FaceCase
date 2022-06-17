package com.android.facecase.pools

import androidx.core.util.Pools
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.locks.ReentrantLock

/**
 * @author：TianLong
 * @date：2022/6/15 17:32
 * @detail：
 */
abstract class Pools<T> :Pool<T> {
    private var lock: ReentrantLock = ReentrantLock()

    protected var queue = ConcurrentLinkedQueue<T>()

    override fun acquire(): T {
        lock.lock()
        try {
            var dataT = queue.poll()

            if (dataT==null){
                dataT = initInstance()
            }
            return dataT
        } finally {
            lock.unlock()
        }
    }

    override fun recycle(instance: T): Boolean {
        return queue.offer(instance)
    }
}