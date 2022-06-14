package com.android.facecase.detail

import android.Manifest
import android.os.Bundle
import android.view.View
import com.android.facecase.R
import com.android.facecase.helper.PermissionHelper
import com.android.facecase.helpkt.ImiCameraHelper
import com.imi.camera.listener.OnOpenCameraListener

class MainActivity : BaseActivity() ,OnOpenCameraListener{
    var isCameraSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ImiCameraHelper.init(this,this)
    }

    override fun onOpenCameraError(p0: String?) {
        showToast(p0)
    }

    override fun onOpenCameraSuccess() {
        showToast("成功")
        isCameraSuccess = true
    }

    fun onNormal(view: View) {
        openActivity(FaceActivity::class.java)
    }

    override fun openActivity(clazz: Class<FaceActivity>){
        if (!PermissionHelper.hasCameraPermission(this)||!PermissionHelper.hasStoragePermission(this)){
            PermissionHelper.requestPermission(this, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            return
        }
        if (isCameraSuccess){
            super.openActivity(clazz)
        }
    }
}