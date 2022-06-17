package com.android.facecase.data

import com.android.facecase.Constant
import com.android.facecase.helpkt.DataHelper
import com.imi.camera.camera.CameraFrame
import com.imi.sdk.facebase.utils.ImageData
import com.imi.sdk.utils.NativeUtils
import java.nio.ByteBuffer

/**
 * @author：TianLong
 * @date：2022/6/14 16:13
 * @detail：正常输入输出（相机实时流彩色深度红外，转成需要数据）
 */
class SampleData :DataHelper<CameraFrame,CameraImage?> {
    lateinit var rgbBuffer :ByteBuffer
    private lateinit var depthBuffer :ByteBuffer
    lateinit var irBuffer : ByteBuffer
    private var cameraImage: CameraImage? = null

    override fun 输入数据(t:CameraFrame, k :CameraImage?) {
        if (k==null){
            rgbBuffer = ByteBuffer.allocateDirect(Constant.IMAGE_WIDTH*Constant.IMAGE_HEIGHT*3)
            depthBuffer = ByteBuffer.allocateDirect(Constant.IMAGE_WIDTH*Constant.IMAGE_HEIGHT*2)
            irBuffer = ByteBuffer.allocateDirect(Constant.IMAGE_WIDTH*Constant.IMAGE_HEIGHT*2)

            val rgbImage = ImageData(Constant.IMAGE_WIDTH,Constant.IMAGE_HEIGHT,ImageData.Type.RGB,rgbBuffer)
            val depthImage = ImageData(Constant.IMAGE_WIDTH,Constant.IMAGE_HEIGHT,ImageData.Type.DEPTH,depthBuffer)
            val irImage = ImageData(Constant.IMAGE_WIDTH,Constant.IMAGE_HEIGHT,ImageData.Type.IR,irBuffer)
            cameraImage = CameraImage(rgbImage,depthImage,irImage)
        }else{
            cameraImage = k
        }
        // k?.代表着 不为空的时候执行，为空不执行
        // k!!.代表着 不为空的时候执行，为空的时候抛异常
        cameraImage?.let {
            NativeUtils.copyByteBufferData(t.colorImage.imageData, it.faceImage.imageData,t.colorImage.dataLength)
            NativeUtils.copyByteBufferData(t.depthImage.imageData, it.depthImage.imageData,t.depthImage.dataLength)
            NativeUtils.copyByteBufferData(t.irImage.imageData, it.irImage.imageData,t.irImage.dataLength)
        }
    }

    override fun 拉取数据(): CameraImage? {
        return cameraImage
    }
}