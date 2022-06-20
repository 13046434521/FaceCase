package com.android.facecase.interfaces

import android.graphics.Rect
import com.android.facecase.data.CameraImage
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.LivenessResult

/**
 * @author：TianLong
 * @date：2022/6/13 17:39
 * @detail：
 */
interface AlgFaceEndInterface {
    fun faceAlgEndInterface(cameraImage: CameraImage?, rect: Rect?, livenessResult: LivenessResult?, quality: FaceQuality?)
}