package com.android.facecase.interfaces

import com.android.facecase.data.CameraImage
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.FaceQuality
import com.imi.sdk.face.Frame
import com.imi.sdk.face.LivenessResult
import com.imi.sdk.facebase.utils.Rect

/**
 * @author：TianLong
 * @date：2022/6/13 17:39
 * @detail：
 */
interface AlgFaceEndInterface {
    fun faceAlgEndInterface(cameraImage: CameraImage?,rect:Rect?,livenessResult: LivenessResult?,quality: FaceQuality?)
}