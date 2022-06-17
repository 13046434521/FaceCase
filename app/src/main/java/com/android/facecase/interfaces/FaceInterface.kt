package com.android.facecase.interfaces

import com.imi.sdk.face.*

/**
 * @author：TianLong
 * @date：2022/6/13 17:05
 * @detail：
 */
interface FaceInterface {
    fun initFace(session: Session?)

    fun startFaceAlg()

    fun detectFace(frame:Frame?):FaceInfo?

    fun detectLiveness(frame:Frame?,faceInfo: FaceInfo):LivenessResult?

    fun detectQuality(frame:Frame?):FaceQuality?

    fun releaseFace(session: Session?)
}
