package com.android.facecase.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.android.facecase.Constant
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.data.LivenessData
import com.android.facecase.helpkt.CameraHelper
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.interfaces.*
import com.android.facecase.pools.CameraImagePools
import com.imi.camera.camera.CameraFrame
import com.imi.camera.listener.OnFrameAvailableListener
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult
import com.imi.sdk.facebase.utils.Rect
import com.jtl.surface.gl.RgbGLSurface

class LivenessActivity : AppCompatActivity() ,OnFrameAvailableListener, AlgInitInterface, AlgFaceInterface, AlgDetectInterface,AlgLivenessInterface, AlgQualityInterface, AlgFaceEndInterface {
    val rgbSurface by lazy<RgbGLSurface> { findViewById(R.id.rgb_liveness_surface) }
    val contentTextView by lazy<TextView> { findViewById(R.id.tv_liveness_content) }
    val dataPools by lazy<CameraImagePools> {return@lazy CameraImagePools()}
    val livenessData:LivenessData = LivenessData()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liveness)
        Constant.IMAGE_WIDTH = 640
        Constant.IMAGE_HEIGHT = 480
        val data =livenessData.拉取数据()
        rgbSurface.updataImage(data.faceImage.imageData,data.faceImage.imageWidth,data.faceImage.imageHeight)
        rgbSurface.requestRender()
    }

    override fun onFrameAvailable(p0: CameraFrame?) {


    }

    override fun detectAlgInterface(faceInfo: FaceInfo?) {
        TODO("Not yet implemented")
    }

    override fun faceAlgEndInterface(
        cameraImage: CameraImage?,
        rect: Rect?,
        livenessResult: LivenessResult?,
        quality: FaceQuality?
    ) {
        TODO("Not yet implemented")
    }

    override fun faceAlgInterface(): CameraImage {
        return livenessData.拉取数据()
    }

    override fun initAlgInterface(code: Int, msg: String, initRes: Boolean) {
        TODO("Not yet implemented")
    }

    override fun livenessAlgInterface(livenessResult: LivenessResult?) {
        TODO("Not yet implemented")
    }

    override fun qualityInterface(quality: FaceQuality?) {
        TODO("Not yet implemented")
    }
}