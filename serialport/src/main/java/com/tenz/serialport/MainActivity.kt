package com.tenz.serialport

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private val requestStorage = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            Toast.makeText(this, "has got the permission", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "has not got the permission", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tvTest = findViewById<TextView>(R.id.tv_test)

        tvTest.setOnClickListener {
            requestStorage.launch(Manifest.permission_group.STORAGE)
        }
    }

}