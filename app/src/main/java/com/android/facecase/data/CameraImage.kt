package com.android.facecase.data

import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.Frame
import com.imi.sdk.facebase.utils.ImageData
import com.imi.sdk.facebase.utils.Rect

/**
 * @author：TianLong
 * @date：2022/3/25 15:45
 * @detail：
 */
data class CameraImage(val faceImage: ImageData, val depthImage: ImageData, val irImage: ImageData, var dataName: String="",var frame: Frame?=null,
                       var mRect: Rect? = null,
                       var mFaceInfo: FaceInfo? = null)