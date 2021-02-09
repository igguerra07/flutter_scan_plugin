package br.com.igguerra.flutter_scanner_plugin

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.ViewfinderView

class ScannerActivity : AppCompatActivity(), TorchListener {

    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var viewFinderView : ViewfinderView
    private lateinit var switchFlashlightButton : FloatingActionButton
    private lateinit var switchFlashlightIcon : ImageView
    private var isFlashlightOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scanner)

        if (savedInstanceState != null) isFlashlightOn = savedInstanceState.getBoolean(FLASH_STATE)

        switchFlashlightButton = findViewById(R.id.floatingFlashButton)
        switchFlashlightIcon = findViewById(R.id.flashIconButton)
        viewFinderView = findViewById(R.id.zxing_viewfinder_view)
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)

        barcodeScannerView.setStatusText("")
        barcodeScannerView.setTorchListener(this)

        switchFlashlightButton.setOnClickListener { switchFlashlight() }

        if (isFlashlightOn) {
            switchFlashlightIcon.setImageResource(R.drawable.ic_flashlight_off)
        } else {
            switchFlashlightIcon.setImageResource(R.drawable.ic_flashlight_on)
        }

        if (!hasFlash()) switchFlashlightButton.visibility = View.GONE

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR

        changeViewFinderColor()
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun switchFlashlight() {
        if(isFlashlightOn) {
            barcodeScannerView.setTorchOff()
        } else {
            barcodeScannerView.setTorchOn()
        }
    }

    private fun changeViewFinderColor() {
        val color  = Color.argb(200,0, 0, 0)
        viewFinderView.setMaskColor(color)
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(FLASH_STATE, isFlashlightOn)
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onTorchOn() {
        isFlashlightOn = true
        switchFlashlightIcon.setImageResource(R.drawable.ic_flashlight_off)
    }

    override fun onTorchOff() {
        isFlashlightOn = false
        switchFlashlightIcon.setImageResource(R.drawable.ic_flashlight_on)
    }

    companion object {
        private const val FLASH_STATE = "flash_state"
    }
}