package com.android.facecase.interfaces

import androidx.appcompat.app.AppCompatActivity
import com.android.facecase.detail.FaceActivity
import com.android.facecase.detail.NormalActivity

/**
 * @author：TianLong
 * @date：2022/6/13 13:52
 * @detail：
 */
interface BaseInterface {
    fun <T>openActivity(clazz: Class<T>)

}