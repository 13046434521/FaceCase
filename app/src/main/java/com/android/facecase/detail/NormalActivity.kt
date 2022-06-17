package com.android.facecase.detail

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.helpkt.CameraHelper
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.imi.camera.camera.CameraFrame
import com.imi.camera.listener.OnFrameAvailableListener
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.jtl.surface.gl.RgbGLSurface

class NormalActivity : FaceActivity() , OnFrameAvailableListener ,AlgInitInterface,AlgFaceInterface,AlgDetectInterface,AlgLivenessInterface,AlgQualityInterface,AlgFaceEndInterface{
    private val rgbGLSurface by lazy<RgbGLSurface> { findViewById(R.id.rgb_normal_surface) }
    private val contentTextView by lazy<TextView> { findViewById(R.id.tv_normal_content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal)

        CameraHelper.setCameraOnFrameAvailable(this)
        FaceHelper.algInitInterface = this
        FaceHelper.algFaceInterface = this
        FaceHelper.algDetectInterface = this
        FaceHelper.algQualityInterface = this
        FaceHelper.algLivenessInterface = this
        FaceHelper.algFaceEndInterface = this
    }

    override fun onFrameAvailable(p0: CameraFrame) {
        cameraPools.clear()
        dataHelper.输入数据(p0,cameraPools.acquire())
        val cameraImage =dataHelper.拉取数据()
        cameraImage?.let {
            rgbGLSurface.updataImage(it.faceImage.imageData, it.faceImage.imageWidth, it.faceImage.imageHeight)
            rgbGLSurface.requestRender()
            FaceHelper.isAlgSuccess
            when{
                FaceHelper.isAlgSuccess-> {facePools.clear()
                    facePools.recycle(it)}
                else->cameraPools.recycle(it)
            }
        }
    }

    override fun initAlgInterface(code: Int, msg: String, initRes: Boolean) {
        showToast(msg)
        FaceHelper.startFaceAlg()
    }

    override fun faceAlgInterface(): CameraImage {
        val cameraImage:CameraImage = facePools.acquire()
        val frame = faceSession.update(cameraImage.faceImage,cameraImage.depthImage,cameraImage.irImage)
        cameraImage.frame = frame
        return cameraImage
    }

    override fun detectAlgInterface(faceInfo: FaceInfo?) {
        val rect = faceInfo?.faceRect
        rgbGLSurface.updateRect(rect?.let { Rect(it.x,it.y,it.x+it.width,it.y+it.height) })
    }

    override fun livenessAlgInterface(livenessResult: LivenessResult?) {
        Log.e(TAG,"活体分值：${livenessResult?.livenessScore}")
    }

    override fun qualityInterface(quality: FaceQuality?) {
        Log.e(TAG,"质量评估：${quality?.quality?.toInfo()}")
    }

    override fun faceAlgEndInterface(
        cameraImage:CameraImage?,
        rect: com.imi.sdk.facebase.utils.Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        cameraImage?.let {
            cameraPools.recycle(it)
        }

        contentTextView.post {
            val stringBuilder = StringBuilder()
            livenessResult?.let {
                stringBuilder.append("活体：${getLivenessMsg(it)}\n ")
            }

            quality?.let {
                stringBuilder.append("质量评估：${it.quality.toInfo()}")
            }

            contentTextView.setText(stringBuilder.toString())
        }
    }

    /**
     * @detail[cn] 活体检测。活体值超过0.5即可认为是活体。
     * @return[cn] 图像帧中人的活体置信度。
     * 0.0--1.0：活体置信度。
     * 0-> 活体检测成功
     * -101-> 深度图错误，如图像数据为空、图像格式错误等
     * -102-> 人脸距离相机模组过远
     * -103-> 人脸距离深度图边缘过近
     * -104-> 人脸距离相机模组过近
     * -201-> 彩色图错误，如图像数据为空、图像格式错误等
     * -202-> 人脸过小
     * -203-> 人脸距离彩色图边缘过近
     * -99-> 人脸算法初始化失败
     * -1-> 未知错误
     */
    private fun getLivenessMsg(livenessResult: LivenessResult): String? {
        var msg: String
        when(livenessResult.errorCode){
            -1->msg = "未知错误"
            -99->msg = "人脸算法初始化失败"
            -101->msg = "深度图错误，如图像数据为空、图像格式错误等"
            -102->msg = "人脸距离相机模组过远"
            -103->msg = "人脸距离深度图边缘过近"
            -104->msg = "图像中人脸距离相机过近"
            -201->msg = "彩色图错误，比如图像为空、格式错误等"
            -202->msg = "图像中人脸过小"
            -203->msg = "人脸距离彩色图边缘过近"
            else-> {
                val liveness = livenessResult.livenessScore
                msg = if (liveness in 0f..0.5f) {
                    "非活体:$liveness"
                } else {
                    "活体:$liveness"
                }
            }
        }
        return msg
    }
}