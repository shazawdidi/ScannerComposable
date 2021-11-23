package com.altkamul.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkPermission
import com.altkamul.scanner.ui.theme.ScannerTheme
import com.mobiiot.androidqapi.api.CsScanner
import java.io.File
import java.security.AccessController.checkPermission
 import java.util.concurrent.ExecutorService

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.compose.material.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.altkamul.scanner.Permission.Companion.checkPermission
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors

class MainActivity  : ComponentActivity() {
    var imageCapture: ImageCapture? = null

    private lateinit var cameraViewModel: MainViewModel
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var qrScanIntegrator: IntentIntegrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScanner()
        Permission.checkPermission(this.applicationContext, this);
        cameraViewModel = MainViewModel(this)
        val x = cameraViewModel.fetchLatestImage();

        setContent {
            ScannerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        floatingActionButton = {
                            performAction()
                        },
                        floatingActionButtonPosition = FabPosition.Center,
                    ) {
                        CameraPreview(
                            buildImageCapture = ::buildImageCapture,
                            cameraViewModel = cameraViewModel
                        )
                    }
                }
            }
        }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun buildImageCapture(_imageCapture: ImageCapture) {
        imageCapture = _imageCapture
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun performAction() {
        // Code to perform action when button is clicked.
        qrScanIntegrator?.initiateScan()
    }
    fun flipCamera() {
        if (cameraViewModel.cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) {
            cameraViewModel.onChangeCameraSelector(CameraSelector.DEFAULT_FRONT_CAMERA)
        } else {
            cameraViewModel.onChangeCameraSelector(CameraSelector.DEFAULT_BACK_CAMERA)

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(
                FILENAME_FORMAT, Locale.US
            ).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_STARTED, savedUri))
                    cameraViewModel.updateLastestImage()
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Permission.REQUEST_CODE_PERMISSIONS) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                // Take the user to the success fragment when permission is granted
                Toast.makeText(
                    this.applicationContext,
                    "Permission request granted",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this.applicationContext,
                    "Permission request denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(this, "no preview", Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {
                    // Converting the data to json format
                    val obj = JSONObject(result.contents)

                    // Show values in UI.
//                    binding.name.text = obj.getString("name")
//                    binding.siteName.text = obj.getString("site_name")

                } catch (e: JSONException) {
                    e.printStackTrace()

                    // Data not in the expected format. So, whole object as toast message.
                    Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
