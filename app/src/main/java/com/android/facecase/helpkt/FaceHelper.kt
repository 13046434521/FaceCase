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
    var isFace:Boolean = false
    var algInitInterface:AlgInitInterface ?= null
    var algDetectInterface:AlgDetectInterface? = null
    var algLivenessInterface:AlgLivenessInterface? = null
    var algQualityInterface:AlgQualityInterface? = null
    var algFaceInterface:AlgFaceInterface? = null
    var algFaceEndInterface:AlgFaceEndInterface? = null
    var algReleaseInterface:AlgReleaseInterface? = null
    lateinit var faceThread:Thread
    override fun initFace(session: Session?) {
        session?.initializeAsync{ i, s ->
            isAlgSuccess =(i==ResultCode.OK)
            Log.d(TAG,"initAlg: $i $s  $isAlgSuccess")

            algInitInterface?.initAlgInterface(i,s, isAlgSuccess)
        }
    }

    override fun startFaceAlg() {
        faceThread = thread {
            isFace = true
            while (isFace){
                val cameraImage =algFaceInterface?.faceAlgInterface()
                val frame = cameraImage?.frame
                var rect: Rect? = null
                var livenessResult: LivenessResult? = null
                var quality: FaceQuality? = null
                algDetectInterface?.let {
                    val faceInfo =detectFace(frame)
                    rect = faceInfo?.faceRect
                    algLivenessInterface?.let {
                        if (faceInfo != null) {
                            livenessResult =  detectLiveness(frame, faceInfo)
                        }
                    }

                    algQualityInterface?.let {
                       quality =  detectQuality(frame)
                    }
                }

                algFaceEndInterface?.faceAlgEndInterface(cameraImage,rect,livenessResult, quality)
            }
        }
    }

    override fun detectFace(frame: Frame?): FaceInfo? {
        val faceInfos = frame?.detectFaces()
        if (faceInfos!=null&& faceInfos.isNotEmpty()){
            val faceInfo = faceInfos.get(0)
            algDetectInterface?.detectAlgInterface(faceInfo)
            return faceInfo
        }

        return null
    }

    override fun detectLiveness(frame: Frame?,faceInfo: FaceInfo) :LivenessResult?{
        val livenessResult = frame?.detectLiveness(faceInfo)

        algLivenessInterface?.livenessAlgInterface(livenessResult)
        return livenessResult
    }

    override fun detectQuality(frame: Frame?):FaceQuality? {
        val faceQuality = frame?.detectFaceQuality()
        algQualityInterface?.qualityInterface(faceQuality)

        return faceQuality
    }


    override fun releaseFace(session: Session?) {
        if (isFace){
            isFace = false
            faceThread.join(1000)
        }
        if (isAlgSuccess){
            session?.release()
            isAlgSuccess = true
        }

        algReleaseInterface?.releaseAlgInterface()
    }
}