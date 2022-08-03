package com.tenz.customview.widget

import android.content.Context
import android.graphics.Canvas

import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.app.ActivityCompat
import com.tenz.customview.R

class CircleView(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private val mContext: Context = context
    private var mPaint: Paint? = null

    init {
        mPaint = Paint()
        mPaint?.color = ActivityCompat.getColor(mContext, R.color.black)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = measuredWidth
        val height = measuredHeight
        val radius = if (width > height) height / 2F else width / 2F
        mPaint?.let { paint ->
            canvas.drawCircle(width / 2F, height / 2F, radius, paint)
//            canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), paint)
        }
    }

}