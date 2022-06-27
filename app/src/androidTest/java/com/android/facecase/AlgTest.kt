package com.android.facecase

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.android.facecase.helper.SessionHelper
import com.android.facecase.helpkt.CameraHelper
import com.imi.camera.listener.OnOpenCameraListener
import com.imi.sdk.face.OnSessionInitializeListener
import com.imi.sdk.face.Session
import com.imi.sdk.facebase.base.ResultCode
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

/**
 * @author：TianLong
 * @date：2022/6/20 22:25
 * @detail：算法初始化测试10000次 单元测试
 */
class AlgTest :OnOpenCameraListener,OnSessionInitializeListener{
    val TAG = this.javaClass.name
    var initSuccess = 0
    var initFail = 0
    val countDownLatch = CountDownLatch(1)

    val cyclicBarrier = CyclicBarrier(2)
    var count = 0
    @Before
    fun before(){
        Log.w(TAG,"before")
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        CameraHelper.initialize(appContext,this)
    }

    @Test
    fun test(){
        countDownLatch.await()
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        for (index in 0 until 10000){
            count++
            cyclicBarrier.reset()
            val session = Session(appContext)
            session.configure(SessionHelper.getInstance().sessionConfig)
            session.initializeAsync(this)
            cyclicBarrier.await()
            session.release()
            Log.w(TAG,"release:${count}")
        }
    }

    override fun onOpenCameraError(p0: String?) {
        countDownLatch.countDown()
        Log.w(TAG,"onOpenCameraError:${p0}")
    }

    override fun onOpenCameraSuccess() {
        countDownLatch.countDown()
        Log.w(TAG,"onOpenCameraSuccess")
    }

    @After
    fun after(){
        Log.w(TAG,"after")
    }

    override fun onSessionInitialized(p0: Int, p1: String?) {
        when (p0) {
            ResultCode.OK -> {
                initSuccess++
            }
            else -> {
                initFail++
            }
        }

        Log.w(TAG,"${p0}:${p1} initSuccess:${initSuccess}  initFail:${initFail} count:${count}")
        cyclicBarrier.await()
    }
}