package br.com.igguerra.flutter_scanner_plugin

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DecoratedBarcodeView.TorchListener
import com.journeyapps.barcodescanner.ViewfinderView

class ScannerActivity : AppCompatActivity(), TorchListener {

    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var viewFinderView : ViewfinderView
    private lateinit var switchFlashlightButton : Button
    private var flashLightState: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scanner)

        flashLightState = if (savedInstanceState != null) {
            savedInstanceState.getString(FLASH_STATE) ?: getString(R.string.turn_on_flashlight)
        } else {
            getString(R.string.turn_on_flashlight)
        }
2
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        barcodeScannerView.setTorchListener(this)

        switchFlashlightButton = findViewById(R.id.switch_flashlight)

        switchFlashlightButton.text = flashLightState

        switchFlashlightButton.setOnClickListener { switchFlashlight() }

        viewFinderView = findViewById(R.id.zxing_viewfinder_view)

        if (!hasFlash()) switchFlashlightButton.visibility = View.GONE

        capture = CaptureManager(this, barcodeScannerView)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    private fun switchFlashlight() {
        if(switchFlashlightButton.text == getString(R.string.turn_off_flashlight)) {
            barcodeScannerView.setTorchOff()
        } else {
            barcodeScannerView.setTorchOn()
        }
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
        outState.putString(FLASH_STATE, switchFlashlightButton.text.toString())
        capture.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onTorchOn() {
        switchFlashlightButton.text = getString(R.string.turn_off_flashlight)
    }

    override fun onTorchOff() {
        switchFlashlightButton.text = getString(R.string.turn_on_flashlight)
    }

    companion object {
        private const val FLASH_STATE = "flash_state"
    }
}