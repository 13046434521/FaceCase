package com.android.facecase.detail

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.android.facecase.Constant
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.data.OfflineData
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.imi.sdk.face.Quality
import com.jtl.surface.gl.DepthGLSurface
import com.jtl.surface.gl.RgbGLSurface
/**
 * @author：TianLong
 * @date：2022/6/22 16:18
 * @detail：质量评估Activity，测试质量评估测试集
 */
class QualityActivity : FaceActivity() ,AlgInitInterface, AlgFaceInterface,AlgDetectInterface,AlgQualityInterface,AlgFaceEndInterface {
    private val rgbSurface by lazy<RgbGLSurface> { findViewById(R.id.rgb_liveness_surface) }
    private val depthSurface by lazy<DepthGLSurface> { findViewById(R.id.depth_liveness_surface) }
    private val irSurface by lazy<DepthGLSurface> { findViewById(R.id.ir_liveness_surface) }

    private val contentTextView by lazy<TextView> { findViewById(R.id.tv_liveness_content) }
    private val qualityData = OfflineData()

    private var qualityTotalTime = 0F
    private var avgQualityTime = 0F
    private var detectCount = 0
    private var detectTotalTime = 0F
    private var avgDetectTime = 0F
    private var qualityCount =0F
    private var qualityGoodCount = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liveness)
        Constant.IMAGE_WIDTH = 640
        Constant.IMAGE_HEIGHT = 480

        FaceHelper.algInitInterface = this
        FaceHelper.algDetectInterface = this
        FaceHelper.algFaceInterface = this
        FaceHelper.algQualityInterface = this
        FaceHelper.algFaceEndInterface = this

        rgbSurface.setOnClickListener {
            unLock()
        }
    }



    override fun faceAlgInterface(): CameraImage {
        val data =qualityData.拉取数据()
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

    override fun faceAlgEndInterface(
        cameraImage: CameraImage?,
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        quality?.run {
            if (this.quality == Quality.GOOD){
                qualityGoodCount++
            }
            Log.d(TAG,"${cameraImage?.dataName}\n quality:${this.quality.toInfo()}  detectTime:${FaceHelper.detectTime}\n qualityTime:${FaceHelper.qualityTime}\n avgDetectTime:${avgDetectTime}ms \n avgQualityTime:${avgQualityTime}ms ")
            showMsg("${cameraImage?.dataName}\n quality:${this.quality.toInfo()}  detectTime:${FaceHelper.detectTime}\n qualityTime:${FaceHelper.qualityTime}\n avgDetectTime:${avgDetectTime}ms \n avgQualityTime:${avgQualityTime}ms ",contentTextView)
        }

        rgbSurface.updateRect(rect)
        rgbSurface.requestRender()
        depthSurface.updateRect(rect)
        depthSurface.requestRender()
        irSurface.updateRect(rect)
        irSurface.requestRender()

        cameraImage?.run {
//            FaceHelper.isFace=qualityCount>=qualityData.arrayList.size
        }

        Log.e(TAG,"${cameraImage?.dataName}  qualityGoodCount:${qualityGoodCount}  totalCount: ${qualityCount} goodRate：${qualityGoodCount.div(qualityCount)*100}%")
        lock()
    }
    override fun onDestroy() {
        super.onDestroy()
        unLock()
        FaceHelper.releaseFace(faceSession)
    }

    override fun qualityInterface(quality: FaceQuality?) {
        qualityCount++
        qualityTotalTime += FaceHelper.qualityTime
        avgQualityTime = qualityTotalTime.div(qualityCount)
    }
}