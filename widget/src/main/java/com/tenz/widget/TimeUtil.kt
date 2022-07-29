package com.tenz.widget

import java.text.SimpleDateFormat
import java.util.*

class TimeUtil {

    companion object {

        const val FORMAT_HOUR_MM_SS = "HH:mm:ss"

        fun formatTime(time: Long): String {
            val simpleDateFormat = SimpleDateFormat(FORMAT_HOUR_MM_SS, Locale.getDefault())
            return simpleDateFormat.format(Date(time))
        }

    }

}