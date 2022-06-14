package com.android.facecase.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.helpkt.FaceHelper
import com.android.facecase.R
import com.android.facecase.helper.SessionHelper
import com.imi.camera.camera.ImiCamera
import com.imi.sdk.face.*

open class FaceActivity : AppCompatActivity() {
    var faceSession: Session? = null
    var camera:ImiCamera = ImiCamera.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face)
        initSession()
    }

    private fun initSession(){
        val sessionConfig = SessionHelper.getInstance().sessionConfig
        sessionConfig.camera = camera
        faceSession = Session(this)
        faceSession!!.configure(sessionConfig)
        FaceHelper.initFace(faceSession)
    }


}