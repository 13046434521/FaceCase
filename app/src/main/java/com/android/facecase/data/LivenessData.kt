package com.android.facecase.data

import android.util.Log
import com.android.facecase.helpkt.DataHelper
import com.android.utils.ImageUtils
import com.imi.camera.camera.CameraFrame
import com.imi.sdk.facebase.utils.ImageData
import java.nio.ByteBuffer
import com.android.facecase.Constant.IMAGE_WIDTH
import com.android.facecase.Constant.IMAGE_HEIGHT
import com.android.facecase.helper.FileHelper
import com.android.utils.ImageUtils.ImreadModes.*
import java.nio.ByteOrder

/**
 * @author：TianLong
 * @date：2022/6/14 16:13
 * @detail：活体数据集（赵芳的那1000张图片）
 */
class LivenessData :DataHelper<CameraFrame,CameraImage?> {
    lateinit var rgbBuffer :ByteBuffer
    lateinit var irRgbBuffer :ByteBuffer
    lateinit var depthBuffer :ByteBuffer
    lateinit var irBuffer : ByteBuffer
    lateinit var rgbPath :String
    lateinit var depthPath :String
    lateinit var irPath :String
    private var count = 0
    var isEndData = false
    val cameraImage: CameraImage by lazy {
        irRgbBuffer = ByteBuffer.allocateDirect(IMAGE_WIDTH * IMAGE_HEIGHT *3).order(ByteOrder.nativeOrder())
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
            cameraImage.dataName = rgbPath
            //opencv 读取jpg,png数据
            ImageUtils.readRgbBufferByOpenCV(rgbPath,rgbBuffer)
            ImageUtils.readBufferByOpenCV(depthPath,depthBuffer, IMREAD_UNCHANGED)
            ImageUtils.readBufferByOpenCV(irPath,irBuffer,IMREAD_UNCHANGED)
            // 2通道红外数据转换成3通道RGB数据
//            ImiUtil.irDataToRgb(irRgbBuffer,ir2Buffer,IMAGE_WIDTH, IMAGE_HEIGHT)
            //拷贝数据 至 cameraImage中
            ImageUtils.copyByteBuffer(rgbBuffer, it.faceImage.imageData)
            ImageUtils.copyByteBuffer(depthBuffer, it.depthImage.imageData)
            ImageUtils.copyByteBuffer(irBuffer, it.irImage.imageData)
        }

        return cameraImage
    }

    private fun handlePath() {
        count++
        rgbPath = "${FileHelper.getInstance().faceTestFolderPath}liveness/${count}_color.jpg"
        rgbPath = when(count){
            in 1 until 10-> rgbPath.replace("${count}","000${count}")
            in 10 until 100->rgbPath.replace("${count}","00${count}")
            in 100 until 1000->rgbPath.replace("${count}","0${count}")
            else->{
                "${FileHelper.getInstance().faceTestFolderPath}liveness/1000_color.jpg"
            }
        }

        depthPath = rgbPath.replace("_color.jpg","_depth.png")
        irPath = rgbPath.replace("_color.jpg","_infrared.png")

        isEndData = rgbPath.contains("1000")

        Log.w("JTL_PATH", "$rgbPath isEndData:$isEndData")
    }
}