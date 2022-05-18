package com.tenz.xcamera

import android.content.Context
import android.widget.Toast

class ToastUtil {

    companion object {

        fun toast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

}