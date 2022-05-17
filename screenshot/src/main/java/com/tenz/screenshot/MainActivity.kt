package com.tenz.screenshot

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ivImage = findViewById<ImageView>(R.id.iv_image)
        val llShowView = findViewById<LinearLayout>(R.id.ll_show_view)
        var llShowViewWidth = 0
        var llShowViewHeight = 0
        llShowView.viewTreeObserver.addOnGlobalLayoutListener {
            llShowViewWidth = llShowView.measuredWidth
            llShowViewHeight = llShowView.measuredHeight
        }
        findViewById<Button>(R.id.btn_shot_activity).setOnClickListener {
            val shotActivityBitmap = ScreenShotUtil.shotActivity(this)
            ivImage.setImageBitmap(shotActivityBitmap)
        }
        findViewById<Button>(R.id.btn_shot_activity_no_status_bar).setOnClickListener {
            val shotActivityBitmap = ScreenShotUtil.shotActivityNoStatusBar(this)
            ivImage.setImageBitmap(shotActivityBitmap)
        }
        findViewById<Button>(R.id.btn_shot_view).setOnClickListener {
            val shotViewBitmap = ScreenShotUtil.shotView(llShowView, llShowViewWidth, llShowViewHeight)
            ivImage.setImageBitmap(shotViewBitmap)

        }
    }

}