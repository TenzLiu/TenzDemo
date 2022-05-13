package com.tenz.watermark

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivImage = findViewById<ImageView>(R.id.iv_image)
        val watermarkBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon_watermark)
        var sourceBitmap: Bitmap? = null
        WatermarkUtil.covertViewToBitmap(ivImage, object: WatermarkUtil.GetViewBitmapCallBack {

            override fun toBitmap(bitmap: Bitmap) {
                sourceBitmap = bitmap
            }

        })
        findViewById<Button>(R.id.btn_watermark_center).setOnClickListener {
            val createWatermark = WatermarkUtil.createWatermarkCenter(sourceBitmap!!, watermarkBitmap, 80, 80)
            ivImage.setImageBitmap(createWatermark)
        }
        findViewById<Button>(R.id.btn_watermark_left_top).setOnClickListener {
            val createWatermarkTopLeft = WatermarkUtil.createWatermarkTopLeft(
                sourceBitmap!!, watermarkBitmap, 20, 0,
                80, 80
            )
            ivImage.setImageBitmap(createWatermarkTopLeft)
        }
        findViewById<Button>(R.id.btn_watermark_right_top).setOnClickListener {
            val createWatermarkTopRight = WatermarkUtil.createWatermarkTopRight(
                sourceBitmap!!, watermarkBitmap, 20, 0,
                80, 80
            )
            ivImage.setImageBitmap(createWatermarkTopRight)
        }
        findViewById<Button>(R.id.btn_watermark_left_bottom).setOnClickListener {
            val createWatermarkBottomLeft = WatermarkUtil.createWatermarkBottomLeft(
                sourceBitmap!!, watermarkBitmap, 20, 0,
                80, 80
            )
            ivImage.setImageBitmap(createWatermarkBottomLeft)
        }
        findViewById<Button>(R.id.btn_watermark_right_bottom).setOnClickListener {
            val createWatermarkBottomRight = WatermarkUtil.createWatermarkBottomRight(
                sourceBitmap!!, watermarkBitmap, 20, 0,
                80, 80
            )
            ivImage.setImageBitmap(createWatermarkBottomRight)
        }
        findViewById<Button>(R.id.btn_source).setOnClickListener {
            ivImage.setImageBitmap(sourceBitmap)
        }
    }

}