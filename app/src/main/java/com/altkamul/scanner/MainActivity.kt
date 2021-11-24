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

    private var qrScanIntegrator: IntentIntegrator? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupScanner()
         cameraViewModel = MainViewModel(this)

        setContent {
            ScannerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                    ) {
                        performAction()
                    }
                }
            }
        }

    }


    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
    }

    private fun performAction() {
         qrScanIntegrator?.initiateScan()
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            // If QRCode has no data.
            if (result.contents == null) {
                Toast.makeText(this, "no barcode info there", Toast.LENGTH_LONG).show()
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


}
