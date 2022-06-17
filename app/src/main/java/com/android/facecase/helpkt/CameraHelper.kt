package com.android.facecase.helpkt

import android.content.Context
import com.android.facecase.Constant
import com.android.facecase.helper.FileHelper
import com.imi.camera.camera.CameraConfig
import com.imi.camera.camera.ImiCamera
import com.imi.camera.listener.OnFrameAvailableListener
import com.imi.camera.listener.OnOpenCameraListener
import com.imi.sdk.facebase.utils.Size

import com.android.facecase.Constant.*

/**
 * @author：TianLong
 * @date：2022/6/15 16:11
 * @detail：
 */
object CameraHelper {
    private var IMAGE_WIDTH = Constant.IMAGE_WIDTH
    private var IMAGE_HEIGHT =Constant.IMAGE_HEIGHT
    lateinit var camera:ImiCamera
    fun initialize(context: Context, onOpenCameraListener: OnOpenCameraListener, path:String = FileHelper.getInstance().faceImiBinFolderPath, isSupportIr: Boolean = true, config: CameraConfig =cameraConfig) {
        camera = ImiCamera.getInstance()
        camera.open(context,path,isSupportIr,onOpenCameraListener)

    }

    // 注意640 * 480分辨率在以上支持列表中，目前算法仅支持该分辨率
    private val cameraConfig: CameraConfig
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
            cameraConfig.depthType = CameraConfig.DepthType.DEPTH_IR
            // ir图是否为8位单通道单色图
            cameraConfig.isOutput8BitIr = false
            return cameraConfig
        }

    fun startStream(){
        camera.startStream()
    }

    fun prepare(width:Int=IMAGE_WIDTH,height:Int = IMAGE_HEIGHT,listener: OnFrameAvailableListener?=null){
        IMAGE_WIDTH =width
        IMAGE_HEIGHT = height
        camera.configure(cameraConfig)
        camera.startStream()
        camera.setFrameAvailableListener(listener)
    }

    fun setCameraOnFrameAvailable(listener: OnFrameAvailableListener){
        camera.setFrameAvailableListener(listener)
    }
}