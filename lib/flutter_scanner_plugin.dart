import 'dart:async';

import 'package:flutter/services.dart';

class FlutterScannerPlugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_scanner_plugin');

  static Future<String> qrCodeScan() async {
    final String qrCodeResult = await _channel.invokeMethod('qrCodeScan');
    return qrCodeResult;
  }
}
