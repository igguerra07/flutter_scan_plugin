package br.com.igguerra.flutter_scanner_plugin

import android.app.Activity
import android.content.Intent
import androidx.annotation.NonNull
import com.google.zxing.integration.android.IntentIntegrator
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** FlutterScannerPlugin */
class FlutterScannerPlugin: FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {

  private lateinit var activity: Activity
  private lateinit var channel : MethodChannel
  private lateinit var pendingResult: Result

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, CHANNEL_NAME)
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when(call.method) {
      QR_CODE_METHOD -> {
        pendingResult = result
        val intent = Intent(activity, ScannerActivity::class.java)
        activity.startActivityForResult(intent, IntentIntegrator.REQUEST_CODE)
      }
      else -> result.notImplemented()
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    if (requestCode == IntentIntegrator.REQUEST_CODE && data != null) {
      if (resultCode == Activity.RESULT_OK) {
        val result = IntentIntegrator.parseActivityResult(resultCode, data)
        pendingResult.success(result.contents);
      } else {
        pendingResult.success(EMPTY_CHAR);
      }
      return true;
    }
    return false;
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {}

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {}

  override fun onDetachedFromActivity() {}

  companion object {
    private const val CHANNEL_NAME = "flutter_scanner_plugin"
    private const val QR_CODE_METHOD = "qrCodeScan"
    private const val EMPTY_CHAR = ""
  }
}

