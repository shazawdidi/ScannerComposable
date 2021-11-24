package com.altkamul.scanner

import android.app.Activity
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.integration.android.IntentIntegrator

class MainViewModel(val context: Activity) : ViewModel() {
    private var qrScanIntegrator: IntentIntegrator? = null

    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(context)
    }

    private fun performAction() {
        qrScanIntegrator?.initiateScan()
    }


}