package com.tenz.screenshot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View

/**
 * 截屏工具类
 */
class ScreenShotUtil {

    companion object {

        /**
         * 截屏activity（包括空白状态栏）
         */
        fun shotActivity(activity: Activity): Bitmap {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(decorView.drawingCache, 0, 0,
                decorView.measuredWidth, decorView.measuredHeight)
            decorView.isDrawingCacheEnabled = false
            decorView.destroyDrawingCache()
            return bitmap
        }

        /**
         * 截屏activity（去掉状态栏）
         */
        fun shotActivityNoStatusBar(activity: Activity): Bitmap {
            val decorView = activity.window.decorView
            decorView.isDrawingCacheEnabled = true
            decorView.buildDrawingCache()
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)
            val statusBarHeight = rect.top
            val display = activity.windowManager.defaultDisplay
            val width = display.width
            val height = display.height
            val bitmap = Bitmap.createBitmap(decorView.drawingCache, 0, statusBarHeight,
                width, height - statusBarHeight)
            decorView.isDrawingCacheEnabled = false
            decorView.destroyDrawingCache()
            return bitmap
        }

        /**
         * 截屏View（包括空白状态栏）
         */
        fun shotView(view: View, viewWidth: Int, viewHeight: Int): Bitmap {
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            canvas.save()
            view.isDrawingCacheEnabled = false
            view.destroyDrawingCache()
            return bitmap
        }

    }

}