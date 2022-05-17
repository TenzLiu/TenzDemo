package com.tenz.watermark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View
import android.view.ViewTreeObserver

/**
 * 图片添加水印工具类
 */
class WatermarkUtil {

    companion object {

        /**
         * 创造图片水印
         * sourceBitmap 原图
         * watermarkBitmap 水印图
         * paddingLeft 水印左边距
         * paddingTop 水印右边距
         */
        fun createWatermark(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                            paddingLeft: Int, paddingTop: Int,
                            watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            var newBitmap: Bitmap? = null
            //创建新位图
            newBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true)
            //创建画布
            val canvas = Canvas(newBitmap)
            //画入画布
            canvas.drawBitmap(sourceBitmap, 0f, 0f, null)
            //设置水印大小
            val newWatermarkBitmap = zoomBitmap(watermarkBitmap, watermarkWidth, watermarkHeight)
            //画布画入水印
            canvas.drawBitmap(newWatermarkBitmap, paddingLeft.toFloat(), paddingTop.toFloat(), null)
            canvas.save()
            canvas.restore()
            return newBitmap
        }

        /**
         * 获取view的bitmap
         */
        fun covertViewToBitmap(view: View, getViewBitmapCallBack: GetViewBitmapCallBack) {
            view.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    val width = view.measuredWidth
                    val height = view.measuredHeight
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    view.draw(canvas)
                    canvas.save()
                    getViewBitmapCallBack.toBitmap(bitmap)
                }
            })
        }

        /**
         * 创造图片水印（中间）
         */
        fun createWatermarkCenter(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                                  watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            return createWatermark(sourceBitmap, watermarkBitmap,
                (sourceBitmap.width - watermarkWidth) / 2,
                (sourceBitmap.height - watermarkHeight) / 2,
            watermarkWidth, watermarkHeight)
        }

        /**
         * 创造图片水印（左上）
         */
        fun createWatermarkTopLeft(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                                   paddingLeft: Int, paddingTop: Int,
                                   watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            return createWatermark(sourceBitmap, watermarkBitmap,
                paddingLeft,
                paddingTop,
            watermarkWidth, watermarkHeight)
        }

        /**
         * 创造图片水印（右上）
         */
        fun createWatermarkTopRight(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                                   paddingLeft: Int, paddingTop: Int,
                                   watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            return createWatermark(sourceBitmap, watermarkBitmap,
                sourceBitmap.width - watermarkWidth - paddingLeft,
                paddingTop,
            watermarkWidth, watermarkHeight)
        }

        /**
         * 创造图片水印（左下）
         */
        fun createWatermarkBottomLeft(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                                   paddingLeft: Int, paddingTop: Int,
                                   watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            return createWatermark(sourceBitmap, watermarkBitmap,
                paddingLeft,
                sourceBitmap.height - watermarkHeight - paddingTop,
            watermarkWidth, watermarkHeight)
        }

        /**
         * 创造图片水印（右下）
         */
        fun createWatermarkBottomRight(sourceBitmap: Bitmap, watermarkBitmap: Bitmap,
                                   paddingLeft: Int, paddingTop: Int,
                                   watermarkWidth: Int, watermarkHeight: Int): Bitmap {
            return createWatermark(sourceBitmap, watermarkBitmap,
                sourceBitmap.width - watermarkWidth - paddingLeft,
                sourceBitmap.height - watermarkHeight - paddingTop,
            watermarkWidth, watermarkHeight)
        }

        /**
         * 将bitmap缩放到指定的大小
         */
        fun zoomBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            // 获取bitmap的宽和高
            val width = bitmap.width.toFloat()
            val height = bitmap.height.toFloat()
            // 创建操作图片用的matrix对象
            val matrix = Matrix()
            // 计算宽高缩放率
            val scaleWidth = newWidth / width
            val scaleHeight = newHeight / height
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(
                bitmap, 0, 0, width.toInt(),
                height.toInt(), matrix, true
            )
        }

    }

    interface GetViewBitmapCallBack{
        fun toBitmap(bitmap: Bitmap)
    }

}