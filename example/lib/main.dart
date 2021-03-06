import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter_scanner_plugin/flutter_scanner_plugin.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _scanResult = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {

    // Platform messages may fail, so we use a try/catch PlatformException.

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Scan Result: $_scanResult\n'),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: _handleQrCodeScan,
        ),
      ),
    );
  }

  void _handleQrCodeScan() async {
    final test = await FlutterScannerPlugin.qrCodeScan();
    setState(() {
      _scanResult = test;
    });
  }
}
