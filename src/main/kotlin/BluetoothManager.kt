package com.ironbird

import com.welie.blessed.*


class BluetoothManager {

    init {
        val callback: BluetoothCentralManagerCallback = object:BluetoothCentralManagerCallback() {
            override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
                println("onConnectedPeripheral: ${peripheral.name}")
            }

            override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
                println("onConnectionFailed: ${peripheral.name} -> ${status.name}")
            }

            override fun onDisconnectedPeripheral(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
                println("onDisconnectedPeripheral: ${peripheral.name}")
            }

            override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
                println("onDiscoveredPeripheral: ${peripheral.name} / ${scanResult.name} / ${scanResult.serviceData}::${scanResult.manufacturerData}")
            }

            override fun onScanStarted() {
            }

            override fun onScanStopped() {
            }

            override fun onScanFailed(errorCode: Int) {
            }
        }
        val bluetoothCentral = BluetoothCentralManager(callback)
        bluetoothCentral.scanForPeripherals()
    }
}