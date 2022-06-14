package com.android.facecase.interfaces

import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.Frame
import com.imi.sdk.face.Session

/**
 * @author：TianLong
 * @date：2022/6/13 17:05
 * @detail：
 */
interface FaceInterface {
    fun initFace(session: Session?)

    fun startFaceAlg()

    fun detectFace(frame:Frame?)

    fun detectLiveness(frame:Frame?,faceInfo: FaceInfo)

    fun detectQuality(frame:Frame?)

    fun releaseFace(session: Session?)
}
