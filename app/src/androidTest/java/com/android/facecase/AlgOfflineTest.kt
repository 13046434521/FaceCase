package com.android.facecase

import android.graphics.Rect
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import com.android.facecase.data.CameraImage
import com.android.facecase.data.OfflineData
import com.android.facecase.helper.SessionHelper
import com.android.facecase.helpkt.CameraHelper
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.imi.camera.listener.OnOpenCameraListener
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.imi.sdk.face.Session
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CyclicBarrier

/**
 * @author：TianLong
 * @date：2022/6/20 22:25
 * @detail：离线Raw数据测试
 */
class AlgOfflineTest :OnOpenCameraListener,AlgFaceInterface,AlgDetectInterface,AlgQualityInterface,AlgInitInterface,AlgFaceEndInterface{
    val TAG = this.javaClass.name
    val countDownLatch = CountDownLatch(1)
    val cyclicBarrier = CyclicBarrier(2)
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    val session by lazy {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val session = Session(appContext)
        val config = SessionHelper.getInstance().sessionConfig
        config.camera = CameraHelper.camera
        session.configure(config)
        Log.w(TAG,"session by lazy")
        return@lazy session
    }

    val offlineData =OfflineData()
    @Before
    fun before(){
        print("before()")
        Log.w(TAG,"before")

        CameraHelper.initialize(appContext,this)
        FaceHelper.algInitInterface = this
        FaceHelper.algDetectInterface = this
        FaceHelper.algFaceInterface = this
        FaceHelper.algQualityInterface = this
        FaceHelper.algFaceEndInterface = this
    }

    @Test
    fun test(){
        countDownLatch.await()
        cyclicBarrier.await()
    }

    override fun onOpenCameraError(p0: String?) {
        countDownLatch.countDown()
        Log.w(TAG,"onOpenCameraError:${p0}")
    }

    override fun onOpenCameraSuccess() {
        countDownLatch.countDown()
        Log.w(TAG,"onOpenCameraSuccess")

        FaceHelper.initFace(session)
    }

    @After
    fun after(){
        Log.w(TAG,"after")
        FaceHelper.releaseFace(session)
    }

    override fun initAlgInterface(code: Int, msg: String, initRes: Boolean) {
        Log.w(TAG,"${code}:${msg} result:${initRes}")
        FaceHelper.startFaceAlg()
    }

    override fun faceAlgInterface(): CameraImage {
        val cameraImage = offlineData.拉取数据()
        cameraImage.frame = session.update(cameraImage.faceImage,cameraImage.depthImage,cameraImage.irImage)
//        cameraImage?.run {
//            val bitmap = Bitmap.createBitmap(this.faceImage.imageWidth,this.faceImage.imageHeight,
//                Bitmap.Config.ARGB_8888)
//            ImiUtil.rgbToBitmap(bitmap,cameraImage.faceImage.imageData)
//            FileHelper.getInstance().saveBitmap(bitmap)
//        }
        return cameraImage
    }

    override fun qualityInterface(quality: FaceQuality?) {
        Log.d(TAG,"质量：${quality?.quality?.toInfo()}")
    }

    override fun faceAlgEndInterface(
        cameraImage: CameraImage?,
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        cameraImage?.run {
            Log.w(TAG,"名字：${this.dataName}  质量：${quality?.quality?.toInfo()}")
        }

        if (offlineData.isOver){
            cyclicBarrier.await()
        }
    }

    override fun detectAlgInterface(faceInfo: FaceInfo?) {
        faceInfo?.run {
            Log.v(TAG,"数量：${faceInfo.faceInfo}")
        }
    }
}