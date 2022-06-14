package com.android.facecase.interfaces

import com.imi.sdk.face.FaceInfo

/**
 * @author：TianLong
 * @date：2022/6/13 17:39
 * @detail：
 */
interface AlgDetectInterface {
    fun detectAlgInterface(faceInfo: FaceInfo?)
}