package com.android.facecase.pools

import com.android.facecase.data.CameraImage
import com.imi.sdk.facebase.utils.ImageData
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author：TianLong
 * @date：2022/6/16 16:32
 * @detail： CameraImage 复用池
 */
class CameraImagePools(width: Int = 480, height: Int = 640) : Pools<CameraImage>() {
    private var imageWidth = width
    private var imageHeight = height

   override fun initInstance(): CameraImage {
        val rgbBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * 3).order(ByteOrder.nativeOrder())
        val depthBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * 2).order(ByteOrder.nativeOrder())
        val irBuffer = ByteBuffer.allocateDirect(imageWidth * imageHeight * 2).order(ByteOrder.nativeOrder())
        val rgbImage = ImageData(imageWidth, imageHeight, ImageData.Type.RGB, rgbBuffer)
        val depthImage = ImageData(imageWidth, imageHeight, ImageData.Type.DEPTH, depthBuffer)
        val irImage = ImageData(imageWidth, imageHeight, ImageData.Type.IR, irBuffer)

        return CameraImage(rgbImage, depthImage, irImage)
    }

    override fun releaseInstances() {
        queue.clear()
    }

    override fun isInPool(t: CameraImage): Boolean {
      return queue.contains(t)
    }
    fun clear(){
        queue.clear()
    }
}