package com.tenz.xcamera

import android.util.Log

class LogUtil {

    companion object {

        private val TAG: String = "tenz"

        fun debug(message: String) {
            Log.d(TAG, message)
        }

    }

}