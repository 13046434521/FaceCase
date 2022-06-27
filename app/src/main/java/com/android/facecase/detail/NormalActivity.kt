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
/**
 * @author：TianLong
 * @date：2022/6/13 13:52
 * @detail：正常使用Activity，模组获取实时流数据
 */
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
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        cameraImage?.let {
            cameraPools.recycle(it)
        }

        contentTextView.post {
            val stringBuilder = StringBuilder()
            stringBuilder.append(faceSession.sdkName)
            livenessResult?.let {
                stringBuilder.append("活体：${getLivenessMsg(it)}\n ")
            }

            quality?.let {
                stringBuilder.append("质量评估：${it.quality.toInfo()}")
            }

            contentTextView.setText(stringBuilder.toString())
        }
    }
}