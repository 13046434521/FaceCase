package com.android.facecase.detail

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.R
import com.android.facecase.helper.PermissionHelper
import com.android.facecase.helpkt.CameraHelper
import com.imi.camera.listener.OnOpenCameraListener

class MainActivity : BaseActivity() ,OnOpenCameraListener{
    var isCameraSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CameraHelper.initialize(this,this)
    }

    override fun onOpenCameraError(p0: String?) {
        showToast(p0)
    }

    override fun onOpenCameraSuccess() {
        showToast("成功")
        isCameraSuccess = true
    }

    fun onNormal(view: View) {
        openActivity(NormalActivity::class.java)
    }

    override fun <T> openActivity (clazz: Class<T>){
        if (!PermissionHelper.hasCameraPermission(this)||!PermissionHelper.hasStoragePermission(this)){
            PermissionHelper.requestPermission(this, arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
            return
        }
        if (isCameraSuccess){
            super.openActivity(clazz)
        }
    }
}