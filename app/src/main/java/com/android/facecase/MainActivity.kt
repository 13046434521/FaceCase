package com.android.facecase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.imi.camera.camera.ImiCamera
import com.imi.camera.listener.OnOpenCameraListener

class MainActivity : AppCompatActivity() ,OnOpenCameraListener{
    var camera = null
    var isCameraSuccess = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var camera = ImiCamera.getInstance()
        camera.open(this,"",true,this)
    }

    fun openActivity(clazz: Class<NormalActivity>) {
        if (isCameraSuccess){
            var intent = Intent(this,clazz)
            startActivity(intent)
        }
    }

    override fun onOpenCameraError(p0: String?) {
        showToast(p0)
    }

    override fun onOpenCameraSuccess() {
        showToast("成功")
    }

    fun onNormal(view: View) {
      openActivity(NormalActivity::class.java)
    }

    fun showToast(msg:String?){
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }
}