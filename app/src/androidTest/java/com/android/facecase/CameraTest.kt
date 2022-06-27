package com.android.facecase

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.facecase.helpkt.CameraHelper
import com.imi.camera.listener.OnOpenCameraListener
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CyclicBarrier

/**
 * @author：TianLong
 * @date：2022/6/20 18:48
 * @detail：相机反复初始化10000次 单元测试
 */
@RunWith(AndroidJUnit4::class)
class CameraTest :OnOpenCameraListener{
    val TAG = this.javaClass.name
    var count = 0
    var cyclicBarrier = CyclicBarrier(2)
    @Before
    fun before(){
        Log.w(TAG,"before")
    }

    @Test
    fun test(){
        Log.w(TAG,"test")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        for (index in 1 until 10000){
            cyclicBarrier.reset()
            CameraHelper.initialize(appContext,this)
            cyclicBarrier.await()
            CameraHelper.camera.closeCamera()
            Log.w(TAG,"closeCamera:${count}")
        }
    }

    @After
    fun after(){
        Log.w(TAG,"after")
    }

    override fun onOpenCameraError(p0: String?) {
        Log.w(TAG,"onOpenCameraError:${p0}")
        cyclicBarrier.await()
    }

    override fun onOpenCameraSuccess() {
        count++
        Log.w(TAG,"onOpenCameraSuccess:${count}")
        cyclicBarrier.await()
    }
}