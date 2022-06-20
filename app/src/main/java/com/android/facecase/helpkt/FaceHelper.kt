package com.android.facecase.helpkt

import android.util.Log
import com.android.facecase.interfaces.*
import com.imi.sdk.face.*
import com.imi.sdk.facebase.base.ResultCode
import com.imi.sdk.facebase.utils.Rect
import kotlin.concurrent.thread

/**
 * @author：TianLong
 * @date：2022/6/13 17:07
 * @detail：
 */
object FaceHelper : FaceInterface {
    var TAG = "FaceTest"
    var isAlgSuccess = false
    var isFace: Boolean = false
    var algInitInterface: AlgInitInterface? = null
    var algDetectInterface: AlgDetectInterface? = null
    var algLivenessInterface: AlgLivenessInterface? = null
    var algQualityInterface: AlgQualityInterface? = null
    var algFaceInterface: AlgFaceInterface? = null
    var algFaceEndInterface: AlgFaceEndInterface? = null
    var algReleaseInterface: AlgReleaseInterface? = null
    lateinit var faceThread: Thread

    var detectTime:Long = 0L
    var livenessTime:Long = 0L
    var qualityTime:Long = 0L
    override fun initFace(session: Session?) {
        session?.initializeAsync { i, s ->
            isAlgSuccess = (i == ResultCode.OK)
            Log.d(TAG, "initAlg: $i $s  $isAlgSuccess")

            algInitInterface?.initAlgInterface(i, s, isAlgSuccess)
        }
    }

    override fun startFaceAlg() {
        faceThread = thread {
            isFace = true
            while (isFace) {
                val cameraImage = algFaceInterface?.faceAlgInterface()
                val frame = cameraImage?.frame
                var rect: Rect?
                var livenessResult: LivenessResult? = null
                var quality: FaceQuality? = null

                val faceInfo = detectFace(frame)
                rect = faceInfo?.faceRect
                algLivenessInterface?.let {
                    if (faceInfo != null) {
                        livenessResult = detectLiveness(frame, faceInfo)
                    }
                }

                algQualityInterface?.let {
                    quality = detectQuality(frame)
                }


                algFaceEndInterface?.faceAlgEndInterface(cameraImage, rect?.let {
                    android.graphics.Rect(
                        it.x,
                        it.y,
                        it.x + it.width,
                        it.y + it.height
                    )
                }, livenessResult, quality)
            }
        }
    }

    override fun detectFace(frame: Frame?): FaceInfo? {
        val tt = System.currentTimeMillis()
        val faceInfos = frame?.detectFaces()
        detectTime = System.currentTimeMillis() - tt
        if (faceInfos != null && faceInfos.isNotEmpty()) {
            val faceInfo = faceInfos[0]
            algDetectInterface?.detectAlgInterface(faceInfo)
            return faceInfo
        }

        return null
    }

    override fun detectLiveness(frame: Frame?, faceInfo: FaceInfo): LivenessResult? {
        val tt = System.currentTimeMillis()
        val livenessResult = frame?.detectLiveness(faceInfo)
        livenessTime = System.currentTimeMillis() - tt

        algLivenessInterface?.livenessAlgInterface(livenessResult)
        return livenessResult
    }

    override fun detectQuality(frame: Frame?): FaceQuality? {
        val tt = System.currentTimeMillis()
        val faceQuality = frame?.detectFaceQuality()
        qualityTime = System.currentTimeMillis() - tt

        algQualityInterface?.qualityInterface(faceQuality)

        return faceQuality
    }


    override fun releaseFace(session: Session?) {
        if (isFace) {
            isFace = false
            faceThread.join(1000)
        }
        if (isAlgSuccess) {
            session?.release()
            isAlgSuccess = true
        }

        algReleaseInterface?.releaseAlgInterface()
    }
}