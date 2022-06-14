package com.android.facecase.helpkt

import android.content.Context
import com.imi.camera.camera.ImiCamera
import com.imi.camera.listener.OnOpenCameraListener
import com.android.facecase.helper.FileHelper
import com.imi.camera.camera.CameraConfig
import com.imi.camera.camera.CameraConfig.DepthType
import com.imi.sdk.facebase.utils.Size

/**
 * @author：TianLong
 * @date：2019/12/1 19:56
 */
object ImiCameraHelper {
    private var IMAGE_WIDTH = 640
    private var IMAGE_HEIGHT = 480
    var imiCamera: ImiCamera? = null

    fun init(context: Context?, onOpenCameraListener: OnOpenCameraListener?, isSupportIr: Boolean=true,config:CameraConfig=cameraConfig) {
        imiCamera = ImiCamera.getInstance()
        imiCamera?.configure(config)
        imiCamera?.open(
            context,
            FileHelper.getInstance().faceImiBinFolderPath,
            isSupportIr,
            onOpenCameraListener
        )
    }

    fun setSize(width: Int, height: Int) {
        IMAGE_WIDTH = width
        IMAGE_HEIGHT = height
    }

    // 注意640 * 480分辨率在以上支持列表中，目前算法仅支持该分辨率
    val cameraConfig: CameraConfig
        get() {
            // 注意640 * 480分辨率在以上支持列表中，目前算法仅支持该分辨率
            val cameraConfig = CameraConfig()
            // 彩色图分辨率
            cameraConfig.colorSize = Size(IMAGE_WIDTH, IMAGE_HEIGHT)
            // 深度图分辨率
            cameraConfig.depthSize = Size(IMAGE_WIDTH, IMAGE_HEIGHT)
            // 摄像头模式
            cameraConfig.cameraMode = CameraConfig.MODE_FRONT_CAM
            // 数据流的类别
            cameraConfig.depthType = DepthType.DEPTH_IR
            // ir图是否为8位单通道单色图
            cameraConfig.isOutput8BitIr = false
            return cameraConfig
        }
}