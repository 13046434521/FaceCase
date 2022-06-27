package com.android.facecase.detail

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.facecase.Constant
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.data.SpoofData
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.jtl.surface.gl.DepthGLSurface
import com.jtl.surface.gl.RgbGLSurface
/**
 * @author：TianLong
 * @date：2022/6/22 16:18
 * @detail：非活体Activity，（Spoof非活体测试集）
 */
class SpoofActivity : FaceActivity() ,AlgInitInterface, AlgFaceInterface,AlgDetectInterface,AlgLivenessInterface,AlgFaceEndInterface {
    private val rgbSurface by lazy<RgbGLSurface> { findViewById(R.id.rgb_liveness_surface) }
    private val depthSurface by lazy<DepthGLSurface> { findViewById(R.id.depth_liveness_surface) }
    private val irSurface by lazy<DepthGLSurface> { findViewById(R.id.ir_liveness_surface) }

    private val contentTextView by lazy<TextView> { findViewById(R.id.tv_liveness_content) }
    private val spoofData = SpoofData()

    private var spoofCount =0F
    private var spoofTotalTime = 0F
    private var avgSpoofTime = 0F
    private var detectCount = 0F
    private var detectTotalTime = 0F
    private var avgDetectTime = 0F
    private var spoofSuccessCount =0F
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
        val data =spoofData.拉取数据()
        data.frame = faceSession.update(data.faceImage,data.depthImage,data.irImage)

        rgbSurface.updataImage(data.faceImage.imageData,data.faceImage.imageWidth,data.faceImage.imageHeight)
        depthSurface.updataImage(data.depthImage.imageData,data.depthImage.imageWidth,data.depthImage.imageHeight)
        irSurface.updataImage(data.irImage.imageData,data.irImage.imageWidth,data.irImage.imageHeight)
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
            spoofCount++
            spoofTotalTime+=FaceHelper.livenessTime
            avgSpoofTime = (spoofTotalTime).div(spoofCount)

            Log.w(TAG,"spoofTime:${FaceHelper.livenessTime} avgSpoofTime:${avgSpoofTime}ms")
        }
    }
    override fun faceAlgEndInterface(
        cameraImage: CameraImage?,
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        livenessResult?.run {
            if (livenessResult.livenessScore in 0.0 .. 0.5){
                spoofSuccessCount ++
            }
            showMsg("${cameraImage?.dataName}\n ${getLivenessMsg(livenessResult)}\n spoofSuccess：${spoofSuccessCount}\n detectTime:${FaceHelper.detectTime}\n livenessTime:${FaceHelper.livenessTime}\n avgDetectTime:${avgDetectTime}ms \n avgSpoofTime:${avgSpoofTime}ms ",contentTextView)
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
//        Log.e(TAG,"${cameraImage?.dataName}  livenessCount:${livenessSuccessCount}  totalCount: ${livenessCount} successRate:${livenessSuccessCount.div(livenessCount)} ")
    }

    override fun onDestroy() {
        super.onDestroy()
        FaceHelper.releaseFace(faceSession)
    }
}