package com.android.facecase.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.Constant
import com.android.facecase.Constant.IMAGE_HEIGHT
import com.android.facecase.Constant.IMAGE_WIDTH
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.R
import com.android.facecase.data.CameraImage
import com.android.facecase.data.SampleData
import com.android.facecase.helper.SessionHelper
import com.android.facecase.helpkt.CameraHelper
import com.android.facecase.interfaces.AlgInitInterface
import com.android.facecase.pools.CameraImagePools
import com.imi.camera.camera.CameraFrame
import com.imi.camera.camera.ImiCamera
import com.imi.camera.listener.OnFrameAvailableListener
import com.imi.sdk.face.*
import com.jtl.surface.gl.RgbGLSurface
/**
 * @author：TianLong
 * @date：2022/6/13 13:52
 * @detail：人脸Activity，实现基本人脸功能
 */
open class FaceActivity : BaseActivity(){
    lateinit var faceSession: Session
    var camera:ImiCamera = ImiCamera.getInstance()
    var dataHelper = SampleData()
    var cameraPools = CameraImagePools(IMAGE_WIDTH, IMAGE_HEIGHT)
    var facePools = CameraImagePools(IMAGE_WIDTH, IMAGE_HEIGHT)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)

        CameraHelper.prepare(IMAGE_WIDTH, IMAGE_HEIGHT)
        initSession()
    }

    private fun initSession(){
        val sessionConfig = SessionHelper.getInstance().sessionConfig
        sessionConfig.camera = camera
        faceSession = Session(this)
        faceSession.configure(sessionConfig)
        FaceHelper.initFace(faceSession)
    }

    override fun onDestroy() {
        super.onDestroy()
        FaceHelper.releaseFace(faceSession)
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
    protected fun getLivenessMsg(livenessResult: LivenessResult): String? {
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