package com.android.facecase.helpkt

import android.util.Log
import com.android.facecase.interfaces.*
import com.imi.sdk.face.FaceInfo
import com.imi.sdk.face.Frame
import com.imi.sdk.face.Session
import com.imi.sdk.facebase.base.ResultCode
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
                algFaceInterface?.faceAlgInterface()
            }
        }

    }

    override fun detectFace(frame: Frame?) {
        val faceInfos = frame?.detectFaces()
        val faceInfo = faceInfos?.get(0)

        algDetectInterface?.detectAlgInterface(faceInfo)
    }

    override fun detectLiveness(frame: Frame?,faceInfo: FaceInfo) {
        val livenessResult = frame?.detectLiveness(faceInfo)

        algLivenessInterface?.livenessAlgInterface(livenessResult)
    }

    override fun detectQuality(frame: Frame?) {
        val faceQuality = frame?.detectFaceQuality()
        algQualityInterface?.qualityInterface(faceQuality)
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