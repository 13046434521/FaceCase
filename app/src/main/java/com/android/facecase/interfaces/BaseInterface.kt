package com.android.facecase.interfaces

import com.android.facecase.detail.FaceActivity
import com.android.facecase.detail.NormalActivity

/**
 * @author：TianLong
 * @date：2022/6/13 13:52
 * @detail：
 */
interface BaseInterface {
    fun openActivity(clazz: Class<NormalActivity>)

}