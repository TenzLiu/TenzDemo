package com.tenz.xcamera

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import android.graphics.Bitmap
import android.graphics.Matrix

import android.media.ExifInterface
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var ivImage: ImageView
    private lateinit var cameraPreView: PreviewView
    private lateinit var tvMessage: TextView

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var imagePreview: Preview? = null
    private var imageAnalysis: ImageAnalysis? = null
    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var isRecordingVideo = false

    companion object {

        const val FILE_NAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        const val PHOTO_SUFFIX = ".jpg"
        const val VIDEO_SUFFIX = ".mp4"
        const val REQUEST_CODE_PERMISSIONS = 101
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )

        fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )

    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (checkAllPermissionGrant()) {
            initCamera()
        }

        ivImage = findViewById(R.id.iv_image)
        cameraPreView = findViewById(R.id.pv_camera_preview)
        tvMessage = findViewById(R.id.tv_message)
        findViewById<Button>(R.id.btn_take_phone).setOnClickListener {
            takePhoto()
        }
        findViewById<Button>(R.id.btn_take_video).setOnClickListener {
            if (isRecordingVideo) {
                isRecordingVideo = false
                videoCapture?.stopRecording()
            } else {
                isRecordingVideo = true
                takeVideo()
            }
        }
        findViewById<Button>(R.id.btn_switch_camera).setOnClickListener {
            switchCamera()
        }

    }

    @SuppressLint("RestrictedApi")
    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        cameraProviderFuture.addListener({
            imagePreview = Preview.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
                setTargetRotation(cameraPreView.display.rotation)
            }.build()
            imageAnalysis = ImageAnalysis.Builder().apply {
                setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            }.build()
            imageAnalysis?.setAnalyzer(cameraExecutor, LuminosityAnalyzer())
            imageCapture = ImageCapture.Builder().apply {
                setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                setFlashMode(ImageCapture.FLASH_MODE_AUTO)
            }.build()
            videoCapture = VideoCapture.Builder().apply {
                setTargetAspectRatio(AspectRatio.RATIO_16_9)
            }.build()
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imagePreview,
//                imageAnalysis,
                imageCapture,
                videoCapture
            )
            cameraPreView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            imagePreview?.setSurfaceProvider(cameraPreView.surfaceProvider)
            cameraControl = camera.cameraControl
            cameraInfo = camera.cameraInfo

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val file = createFile(getOutputDirectory(), FILE_NAME_FORMAT, PHOTO_SUFFIX)
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        if (checkAllPermissionGrant()) {
            imageCapture?.takePicture(outputFileOptions, cameraExecutor, object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    cameraPreView.post {
                        tvMessage.text = "onImageSaved: ${file.absolutePath}"
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        ivImage.setImageBitmap(rotaImageView(readPictureDegree(file.absolutePath), bitmap))
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    cameraPreView.post {
                        tvMessage.text = "onError: ${exception.toString()}"
                    }
                }

            })
        }
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun takeVideo() {
        val file = createFile(getOutputDirectory(), FILE_NAME_FORMAT, VIDEO_SUFFIX)
        val outputFileOptions = VideoCapture.OutputFileOptions.Builder(file).build()
        if (checkAllPermissionGrant()) {
            videoCapture?.startRecording(outputFileOptions, cameraExecutor, object: VideoCapture.OnVideoSavedCallback{
                override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                    cameraPreView.post {
                        tvMessage.text = "onImageSaved: ${file.absolutePath}"
                    }
                }

                override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                    cameraPreView.post {
                        tvMessage.text = "onError: ${message}"
                    }
                }

            })
        }
    }

    private fun switchCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
        initCamera()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (checkAllPermissionGrant()) {
                initCamera()
            } else {
                ToastUtil.toast(this, "Permissions not granted by the user.")
                finish()
            }
        }
    }

    fun getOutputDirectory(): File {
        val mediaDirs = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if(mediaDirs != null && mediaDirs.exists()) mediaDirs else filesDir
    }

    private class LuminosityAnalyzer : ImageAnalysis.Analyzer {
        private var lastAnalyzedTimestamp = 0L

        /**
         * Helper extension function used to extract a byte array from an
         * image plane buffer
         */
        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {
            image.imageInfo.rotationDegrees
            val currentTimestamp = System.currentTimeMillis()
            // Calculate the average luma no more often than every second
            if (currentTimestamp - lastAnalyzedTimestamp >=
                TimeUnit.SECONDS.toMillis(1)
            ) {
                // Since format in ImageAnalysis is YUV, image.planes[0]
                // contains the Y (luminance) plane
                val buffer = image.planes[0].buffer
                // Extract image data from callback object
                val data = buffer.toByteArray()
                // Convert the data into an array of pixel values
                val pixels = data.map { it.toInt() and 0xFF }
                // Compute average luminance for the image
                val luma = pixels.average()
                // Log the new luma value
                Log.d("CameraXApp", "Average luminosity: $luma")
                // Update timestamp of last analyzed frame
                lastAnalyzedTimestamp = currentTimestamp
            }
            image.close()
        }
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return degree
    }


    /*
    * 旋转图片
    * @param angle
    * @param bitmap
    * @return Bitmap
    */
    fun rotaImageView(angle: Int, bitmap: Bitmap): Bitmap {
        //旋转图片 动作
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}