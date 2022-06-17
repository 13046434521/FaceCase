package com.android.facecase.interfaces

import com.android.facecase.data.CameraImage
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.Frame

/**
 * @author：TianLong
 * @date：2022/6/13 17:39
 * @detail：
 */
interface AlgDetectInterface {
    fun detectAlgInterface(faceInfo: FaceInfo?)
}