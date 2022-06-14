package com.android.facecase.interfaces

import com.imi.sdk.face.LivenessResult

/**
 * @author：TianLong
 * @date：2022/6/13 17:39
 * @detail：
 */
interface AlgLivenessInterface {
    fun livenessAlgInterface(livenessResult: LivenessResult?)
}