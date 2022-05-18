package com.tenz.xcamera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraPreView: PreviewView

    companion object {
        const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val REQUEST_CODE_PERMISSIONS = 101
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkAllPermissionGrant()

        cameraPreView = findViewById(R.id.pv_camera_preview)
        findViewById<Button>(R.id.btn_take_phone).setOnClickListener {
            takePhoto()
        }


    }

    private fun initCamera() {
        val newSingleThreadExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val get = cameraProviderFuture.get()
            Preview.Builder().build().also {
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {

    }

    private fun takeVideo() {

    }

    private fun switchCamera() {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkAllPermissionGrant()) {
                //TODO

            } else {
                ToastUtil.toast(this, "Permissions not granted by the user.")
                finish()
            }
        }
    }

    fun checkAllPermissionGrant(): Boolean {
        val allPermissionsGrant = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        if (!allPermissionsGrant) {
            requestAllPermissions()
        }
        return allPermissionsGrant
    }

    fun requestAllPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

}