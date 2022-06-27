package com.android.facecase.detail

import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.interfaces.BaseInterface
import java.lang.Exception
import java.util.*
import java.util.concurrent.locks.LockSupport
import java.util.concurrent.locks.ReentrantLock

/**
 * @author：TianLong
 * @date：2022/6/13 13:52
 * @detail：Base基类Activity
 */
abstract class BaseActivity : AppCompatActivity(), BaseInterface {
    var TAG = "BaseActivity"
    private val lock = Object()
    override fun <T> openActivity(clazz: Class<T>) {
        val intent = Intent(this,clazz)
        startActivity(intent)
    }

    fun showToast(msg:String?){
        runOnUiThread { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }

    fun showMsg(msg:String?,textView: TextView){
        textView.post {
            textView.setText(msg)
        }
    }

    fun lock(){
        synchronized(lock){
            try {
                lock.wait()
            }catch (e:Exception){
                Log.e(TAG, e.toString())
            }
        }
    }

    fun unLock(){
        synchronized(lock){
            lock.notifyAll()
        }
    }
}