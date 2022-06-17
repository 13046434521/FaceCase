package com.android.facecase.detail

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.interfaces.BaseInterface

open abstract class BaseActivity : AppCompatActivity(), BaseInterface {
    var TAG = "BaseActivity"

    override fun openActivity(clazz: Class<NormalActivity>) {
        val intent = Intent(this,clazz)
        startActivity(intent)
    }

    fun showToast(msg:String?){
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }
}