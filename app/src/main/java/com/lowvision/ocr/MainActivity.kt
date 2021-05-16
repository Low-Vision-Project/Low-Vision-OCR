package com.lowvision.ocr

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var imageUri: Uri? = null
    private var camera: Camera? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Gallery Access
        galleryButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, REQUEST_CODE_PERMISSIONS_GALLERY)
                } else {
                    accessGallery()
                }
            } else {
                accessGallery()
            }

        }


        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS_CAMERA
            )
        }

        // set on click listener for the button of capture photo
        // it calls a method which is implemented below
        camera_capture_button.setOnClickListener {
            takePhoto()

        }
        flashToggle.setOnClickListener {
            flash()
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun readFromGallery(uri: Uri) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(baseContext, uri)

            val recognizer = TextRecognition.getClient()

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully

                    val intent = Intent(this@MainActivity, TextActivity::class.java)
                    intent.putExtra(photoResult, visionText.text)
                    startActivity(intent)

                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e("Main Activity", e.toString())
                }

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun flash() {
        if (flashToggle.isSelected) {
            camera!!.cameraControl.enableTorch(false)
            flashToggle.isSelected = false
            flashToggle.setImageResource(R.drawable.ic_baseline_flash_off_24)
        } else {
            flashToggle.isSelected = true
            camera!!.cameraControl.enableTorch(true)
            flashToggle.setImageResource(R.drawable.ic_baseline_flash_on_24)
        }
    }

    private fun accessGallery() {
        val pickIntent = Intent(Intent.ACTION_PICK)
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        try {
            startActivityForResult(pickIntent, REQUEST_CODE_PERMISSIONS_IMAGE)

        } catch (e: Exception) {
        }
    }

    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Set up image capture listener,
        // which is triggered after photo has been taken
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                @SuppressLint("UnsafeOptInUsageError")
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    val image: InputImage = InputImage.fromMediaImage(
                        imageProxy.image!!,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    val recognizer = TextRecognition.getClient()

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Task completed successfully

                            val intent = Intent(this@MainActivity, TextActivity::class.java)
                            intent.putExtra(photoResult, visionText.text)
                            startActivity(intent)

                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            Log.e("Main Activity", e.toString())
                        }
                }
            })

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera

                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )


            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS_CAMERA) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        } else if (requestCode == REQUEST_CODE_PERMISSIONS_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessGallery()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PERMISSIONS_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            imageUri?.let { readFromGallery(it) }
        }
    }

    companion object {
        const val photoResult: String = "photo_result"
        private const val TAG = "CameraX"
        private const val REQUEST_CODE_PERMISSIONS_CAMERA = 20
        private const val REQUEST_CODE_PERMISSIONS_GALLERY = 100
        private const val REQUEST_CODE_PERMISSIONS_IMAGE = 101

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

}