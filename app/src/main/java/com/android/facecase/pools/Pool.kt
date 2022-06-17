package com.android.facecase.pools

import java.util.*

/**
 * @author：TianLong
 * @date：2022/6/16 15:19
 * @detail：
 */
interface Pool<T> {
    /**
     * @return 返回实例
     */
    fun acquire(): T?

    /**
     * @return 返回实例
     */
    fun recycle(instance: T): Boolean

    /**
     * @return 返回实例
     */
    fun initInstance():T

    /**
     * 释放所有实例
     */
    fun releaseInstances()
    /**
     * @return 对象是否在池中
     */
    fun isInPool(t:T):Boolean
}