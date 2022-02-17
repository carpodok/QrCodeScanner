package com.alitalhacoban.qrcodescanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.IOException

class ScanPageActivity : AppCompatActivity() {

    lateinit var surfaceView: SurfaceView
    lateinit var cameraSource: CameraSource
    lateinit var scanner: View

    private val REQUEST_CAMERA_PERMISSION = 201

    lateinit var barcodeData: String

    lateinit var scanAnim: Animation

    override fun onCreate(savedInstanceState: Bundle?) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_page)

        surfaceView = findViewById(R.id.surface_view)
        scanner = findViewById(R.id.scannerView)
        scanner.bringToFront()

        scanAnim = AnimationUtils.loadAnimation(this@ScanPageActivity, R.anim.scan_anim)

        detectBarcode()
    }

    private fun detectBarcode() {

        scanner.startAnimation(scanAnim)

        val barcodeDetector = BarcodeDetector.Builder(applicationContext)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()


        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()


        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@ScanPageActivity,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource.start(surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@ScanPageActivity,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })

        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {

            override fun release() {

            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    if (barcodes.valueAt(0).email != null) {
                        barcodeData = barcodes.valueAt(0).email.address
                    } else {
                        barcodeData = barcodes.valueAt(0).displayValue
                    }
                    intentToBarcodeLinkActivity(barcodeData)
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
       // supportActionBar!!.hide()
        cameraSource.release()
    }

    override fun onResume() {
        super.onResume()
       // supportActionBar!!.hide()
        detectBarcode()
    }

    private fun intentToBarcodeLinkActivity(transferLink: String) {
        val intent = Intent(this@ScanPageActivity, LinkPageActivity::class.java)
        intent.putExtra("link", transferLink)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }


}