package com.tenz.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import java.util.*

class UpdateWidgetService: Service() {

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        timer = Timer()
        timerTask = object: TimerTask(){

            override fun run() {
                val componentName = ComponentName(this@UpdateWidgetService, MyAppWidgetProvider::class.java)
                val remoteViews = RemoteViews(packageName, R.layout.my_app_widget_provider)
                remoteViews.setTextViewText(R.id.tv_time, TimeUtil.formatTime(System.currentTimeMillis()))
                //刷新点击意图
                val intentClick = Intent()
                intentClick.setClass(this@UpdateWidgetService, MyAppWidgetProvider::class.java)
                intentClick.action = MyAppWidgetProvider.REFRESH_ACTION
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(this@UpdateWidgetService, 0, intentClick, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getBroadcast(this@UpdateWidgetService, 0, intentClick, PendingIntent.FLAG_ONE_SHOT)
                }
                remoteViews.setOnClickPendingIntent(R.id.iv_refresh, pendingIntent)
                //更新部件
                val appWidgetManager = AppWidgetManager.getInstance(this@UpdateWidgetService)
                appWidgetManager.updateAppWidget(componentName, remoteViews)
            }

        }
        timer?.schedule(timerTask, 0,1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timerTask?.cancel()
        timer = null
        timerTask = null
    }

}