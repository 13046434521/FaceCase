package com.android.facecase.detail

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.facecase.Constant
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.data.LivenessData
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.jtl.surface.gl.DepthGLSurface
import com.jtl.surface.gl.RgbGLSurface

class LivenessActivity : FaceActivity() ,AlgInitInterface, AlgFaceInterface,AlgDetectInterface,AlgLivenessInterface,AlgFaceEndInterface {
    private val rgbSurface by lazy<RgbGLSurface> { findViewById(R.id.rgb_liveness_surface) }
    private val depthSurface by lazy<DepthGLSurface> { findViewById(R.id.depth_liveness_surface) }
    private val irSurface by lazy<DepthGLSurface> { findViewById(R.id.ir_liveness_surface) }

    private val contentTextView by lazy<TextView> { findViewById(R.id.tv_liveness_content) }
    private val livenessData:LivenessData = LivenessData()

    private var livenessCount =0L
    private var livenessTotalTime = 0L
    private var avgLiveTime = 0L
    private var detectCount = 0
    private var detectTotalTime = 0L
    private var avgDetectTime = 0L
    private var livenessSuccessCount =0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liveness)
        Constant.IMAGE_WIDTH = 640
        Constant.IMAGE_HEIGHT = 480

        FaceHelper.algInitInterface = this
        FaceHelper.algDetectInterface = this
        FaceHelper.algFaceInterface = this
        FaceHelper.algLivenessInterface = this
        FaceHelper.algFaceEndInterface = this
    }



    override fun faceAlgInterface(): CameraImage {
        val data =livenessData.拉取数据()
        data.frame = faceSession.update(data.faceImage,data.depthImage,data.irImage)

        rgbSurface.updataImage(data.faceImage.imageData,data.faceImage.imageWidth,data.faceImage.imageHeight)
        depthSurface.updataImage(data.depthImage.imageData,data.depthImage.imageWidth,data.depthImage.imageHeight)
        irSurface.updataImage(data.irImage.imageData,data.irImage.imageWidth,data.irImage.imageHeight)
        rgbSurface.requestRender()
        depthSurface.requestRender()
        irSurface.requestRender()
        return data
    }

    override fun detectAlgInterface(faceInfo: FaceInfo?) {
        faceInfo?.run {
            detectCount++
            detectTotalTime+=FaceHelper.detectTime
            avgDetectTime = (detectTotalTime).div(detectCount)
            Log.w(TAG,"detectTime:${FaceHelper.detectTime} avgDetectTime:${avgDetectTime}ms")
        }
    }
    override fun initAlgInterface(code: Int, msg: String, initRes: Boolean) {
        showMsg("$msg $code",contentTextView)
        FaceHelper.startFaceAlg()
    }

    override fun livenessAlgInterface(livenessResult: LivenessResult?) {
        livenessResult?.run {
            livenessCount++
            livenessTotalTime+=FaceHelper.livenessTime
            avgLiveTime = (livenessTotalTime).div(livenessCount)

            Log.w(TAG,"livenessTime:${FaceHelper.livenessTime} avgLiveTime:${avgLiveTime}ms")
        }
    }
    override fun faceAlgEndInterface(
        cameraImage: CameraImage?,
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        livenessResult?.run {
            if (livenessResult.livenessScore in 0.5..1.0){
                livenessSuccessCount ++
            }
            showMsg("${cameraImage?.dataName}\n ${getLivenessMsg(livenessResult)}\n livenessSuccess：${livenessSuccessCount}\n detectTime:${FaceHelper.detectTime}\n livenessTime:${FaceHelper.livenessTime}\n avgDetectTime:${avgDetectTime}ms \n avgLiveTime:${avgLiveTime}ms ",contentTextView)
        }

        rgbSurface.updateRect(rect)
        rgbSurface.requestRender()
        depthSurface.updateRect(rect)
        depthSurface.requestRender()
        irSurface.updateRect(rect)
        irSurface.requestRender()

        cameraImage?.run {
            FaceHelper.isFace = !dataName.contains("1000")
        }
        Log.e(TAG,"${cameraImage?.dataName}  livenessCount:${livenessSuccessCount}  totalCount: ${livenessCount} successRate:${livenessSuccessCount.div(livenessCount)} ")
    }
    override fun onDestroy() {
        super.onDestroy()
        FaceHelper.releaseFace(faceSession)
    }
}