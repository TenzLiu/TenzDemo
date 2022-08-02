package com.tenz.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast

class MyAppWidgetProvider: AppWidgetProvider() {

    companion object {

        //系统更新广播
        const val APPWIDGET_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE"

        //自定义的刷新广播
        const val REFRESH_ACTION = "android.appwidget.action.APPWIDGET_REFRESH"

    }

    /**
     * 接收一次广播消息就调用一次
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action.equals(REFRESH_ACTION)) {
            //点击刷新或通知刷新
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val remoteViews = RemoteViews(context.packageName, R.layout.my_app_widget_provider)
            remoteViews.setTextViewText(R.id.tv_time, TimeUtil.formatTime(System.currentTimeMillis()))
            //更新部件
            appWidgetManager.updateAppWidget(
                ComponentName(
                    context,
                    MyAppWidgetProvider::class.java
                ), remoteViews)
            Toast.makeText(context, "刷新成功", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 到达指定的更新时间或者当用户向桌面添加AppWidget时被调用,或更新widget时
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * 删除一个AppWidget时调用
     */
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
    }

    /**
     * 当该Widget第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        val updateWidgetServiceIntent = Intent(context, UpdateWidgetService::class.java)
        context.startService(updateWidgetServiceIntent)
    }

    /**
     * 当最后一个该Widget删除是调用该方法，注意是最后一个
     */
    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        val updateWidgetServiceIntent = Intent(context, UpdateWidgetService::class.java)
        context.stopService(updateWidgetServiceIntent)
    }

}