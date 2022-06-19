package com.android.facecase.data

import android.graphics.Bitmap
import com.android.facecase.Constant
import com.android.facecase.helpkt.DataHelper
import com.android.utils.ImageUtils
import com.imi.camera.camera.CameraFrame
import com.imi.sdk.facebase.utils.ImageData
import com.imi.sdk.faceid.dl.internal.ImiUtil
import com.imi.sdk.utils.NativeUtils
import java.nio.ByteBuffer
import com.android.facecase.Constant.IMAGE_WIDTH
import com.android.facecase.Constant.IMAGE_HEIGHT
import com.android.facecase.helper.FileHelper
import com.android.utils.ImageUtils.ImreadModes.*
import com.imi.sdk.faceid.dl.internal.ImiUtil.*
import com.imi.sdk.utils.NativeUtils.copyByteBufferData
import java.nio.ByteOrder

/**
 * @author：TianLong
 * @date：2022/6/14 16:13
 * @detail：正常输入输出（相机实时流彩色深度红外，转成需要数据）
 */
class LivenessData :DataHelper<CameraFrame,CameraImage?> {
    lateinit var rgbBuffer :ByteBuffer
    lateinit var bgrBuffer :ByteBuffer
    lateinit var depthBuffer :ByteBuffer
    lateinit var irBuffer : ByteBuffer
    lateinit var rgbPath :String
    lateinit var depthPath :String
    lateinit var irPath :String
    private var count = "0001"
    val cameraImage: CameraImage by lazy {
        bgrBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT *3).order(ByteOrder.nativeOrder())
        rgbBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT *3).order(ByteOrder.nativeOrder())
        depthBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT *2).order(ByteOrder.nativeOrder())
        irBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT *2).order(ByteOrder.nativeOrder())

        val rgbImage = ImageData(IMAGE_WIDTH, IMAGE_HEIGHT,ImageData.Type.RGB,rgbBuffer)
        val depthImage = ImageData(IMAGE_WIDTH, IMAGE_HEIGHT,ImageData.Type.DEPTH,depthBuffer)
        val irImage = ImageData(IMAGE_WIDTH, IMAGE_HEIGHT,ImageData.Type.IR,irBuffer)
        CameraImage(rgbImage,depthImage,irImage)
    }

    override fun 输入数据(t:CameraFrame, k :CameraImage?) {

    }
    //        IMREAD_UNCHANGED            = -1, //!< If set, return the loaded image as is (with alpha channel, otherwise it gets cropped).
    //        IMREAD_GRAYSCALE            = 0,  //!< If set, always convert image to the single channel grayscale image (codec internal conversion).
    //        IMREAD_COLOR                = 1,  //!< If set, always convert image to the 3 channel BGR color image.
    override fun 拉取数据(): CameraImage {
        handlePath()


        // k?.代表着 不为空的时候执行，为空不执行
        // k!!.代表着 不为空的时候执行，为空的时候抛异常
        cameraImage.let {
            // opengcv读取为bgr，需要转换为rgb
            ImageUtils.readRgbBufferByOpenCV(rgbPath,rgbBuffer)
            ImageUtils.readBufferByOpenCV(depthPath,depthBuffer, IMREAD_UNCHANGED)
            ImageUtils.readBufferByOpenCV(irPath,irBuffer,IMREAD_UNCHANGED)
//            ImageUtils.bgr2rgb(bgrBuffer,rgbBuffer,IMAGE_WIDTH, IMAGE_HEIGHT)

            ImageUtils.copyByteBuffer(rgbBuffer, it.faceImage.imageData)
            ImageUtils.copyByteBuffer(depthBuffer, it.depthImage.imageData)
            ImageUtils.copyByteBuffer(irBuffer, it.irImage.imageData)
        }

        return cameraImage
    }

    private fun handlePath() {
        rgbPath = "${FileHelper.getInstance().faceTestFolderPath}${count}_color.jpg"
        depthPath = rgbPath.replace("_color.jpg",".depth.png")
        irPath = rgbPath.replace("_color.jpg",".infrared.png")
    }
}