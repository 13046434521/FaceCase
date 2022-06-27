package com.android.facecase.data

import android.util.Log
import com.android.facecase.Constant
import com.android.facecase.Constant.*
import com.android.facecase.helpkt.DataHelper
import com.android.utils.ImageUtils
import com.imi.camera.camera.CameraFrame
import com.imi.sdk.facebase.utils.ImageData
import java.nio.ByteBuffer
import com.android.facecase.helper.FileHelper
import com.android.utils.ImageUtils.ImreadModes.*
import java.nio.ByteOrder

/**
 * @author：TianLong
 * @date：2022/6/22 17:25
 * @detail：离线数据获取（raw数据）
 */
class OfflineData(var width: Int = 640, var height :Int = 480) :DataHelper<CameraFrame,CameraImage?> {
    lateinit var rgbBuffer :ByteBuffer
    lateinit var irRgbBuffer :ByteBuffer
    lateinit var depthBuffer :ByteBuffer
    lateinit var irBuffer : ByteBuffer
    lateinit var rgbPath :String
    lateinit var depthPath :String
    lateinit var irPath :String
    private var count = 0
    private var WIDTH = width
    private var HEIGHT = height
    @Volatile
    var isOver = false
    val arrayList by lazy {
       return@lazy FileHelper.getInstance().getFiles(FileHelper.getInstance().faceTestFolderPath+"offline/","_rgb.raw")
    }

    val cameraImage: CameraImage by lazy {
        Log.w(TAG,"width:$WIDTH  height:$height")
        irRgbBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT *3).order(ByteOrder.nativeOrder())
        rgbBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT *3).order(ByteOrder.nativeOrder())
        depthBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT *2).order(ByteOrder.nativeOrder())
        irBuffer = ByteBuffer.allocateDirect(WIDTH * HEIGHT *2).order(ByteOrder.nativeOrder())

        val rgbImage = ImageData(WIDTH, HEIGHT,ImageData.Type.RGB,rgbBuffer)
        val depthImage = ImageData(WIDTH, HEIGHT,ImageData.Type.DEPTH,depthBuffer)
        val irImage = ImageData(WIDTH, HEIGHT,ImageData.Type.IR,irBuffer)
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
            // 本地读取二进制文件，opencv读取报错
            FileHelper.getInstance().readLocalFileByteBuffer(rgbPath,640*480*3,rgbBuffer)
            FileHelper.getInstance().readLocalFileByteBuffer(depthPath,640*480*2,depthBuffer)
            FileHelper.getInstance().readLocalFileByteBuffer(irPath,640*480*2,irBuffer)
            //拷贝数据 至 cameraImage中
            ImageUtils.copyByteBuffer(rgbBuffer, it.faceImage.imageData)
            ImageUtils.copyByteBuffer(depthBuffer, it.depthImage.imageData)
            ImageUtils.copyByteBuffer(irBuffer, it.irImage.imageData)
        }

        return cameraImage
    }

    private fun handlePath() {
        if (count<arrayList.size){
            rgbPath = arrayList[count]
            depthPath = rgbPath.replace("_rgb.raw","_depth.raw")
            irPath = rgbPath.replace("_rgb.raw","_ir.raw")
        }else{
            isOver = true
        }
        count++
        Log.w("JTL_PATH","$rgbPath count:$count  size:${arrayList.size} isOver:$isOver  ")
    }
}