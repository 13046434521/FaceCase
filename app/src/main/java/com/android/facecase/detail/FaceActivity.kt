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
}