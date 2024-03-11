package com.ironbird

import com.welie.blessed.*
import java.util.*

private enum class S4Service(val uuid: UUID) {
    DEVICE_INFORMATION(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")),
    GATT_PROFILE(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")),
    BATTERY(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb")),
    HEART_RATE(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")),
}

private enum class S4Characteristics(val service:S4Service, val uuid: UUID) {

    BATTERY_LEVEL(S4Service.BATTERY, UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")),
    HEART_RATE_MEASUREMENT(S4Service.HEART_RATE, UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")),
    ROWER_DATA(S4Service.FITNESS_MACHINE, UUID.fromString("00002ad1-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE_STATUS(S4Service.FITNESS_MACHINE, UUID.fromString("00002ada-0000-1000-8000-00805f9b34fb")),
}

private enum class S4Descriptors(val characteristics: S4Characteristics, val uuid: UUID) {

}

private class S4PeripheralCallBack: BluetoothPeripheralCallback() {
    override fun onServicesDiscovered(peripheral: BluetoothPeripheral, services: MutableList<BluetoothGattService>) {
        println("onServicesDiscovered")
//        services.forEach { service ->
//            println("   -> Discovered service ${service.uuid}")
//            service.characteristics.forEach { characteristic ->
//                println("      > Service ${service.uuid} has characteristic ${characteristic.uuid}... Reading...")
//                characteristic.descriptors.forEach { descriptor ->
//                    println("         > Characteristic ${characteristic.uuid} (service ${service.uuid}) has descriptor ${descriptor.uuid}")
//                }
//                peripheral.readCharacteristic(service.uuid, characteristic.uuid)
//            }
//        }
    }

    override fun onNotificationStateUpdate(
        peripheral: BluetoothPeripheral,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        println("onNotificationStateUpdate")
    }

    override fun onCharacteristicUpdate(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        println("onCharacteristicUpdate ${peripheral.name}::${characteristic.service?.uuid ?: "NO_SERVICE"}::${characteristic.uuid} = $value")
    }

    override fun onCharacteristicWrite(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        println("onCharacteristicWrite")
    }

    override fun onDescriptorRead(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        descriptor: BluetoothGattDescriptor,
        status: BluetoothCommandStatus
    ) {
        println("onDescriptorRead")
    }

    override fun onDescriptorWrite(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        descriptor: BluetoothGattDescriptor,
        status: BluetoothCommandStatus
    ) {
        println("onDescriptorWrite")
    }

    override fun onBondingStarted(peripheral: BluetoothPeripheral) {
        println("onBondingStarted")
    }

    override fun onBondingSucceeded(peripheral: BluetoothPeripheral) {
        println("onBondingSucceeded")
    }

    override fun onBondingFailed(peripheral: BluetoothPeripheral) {
        println("onBondingFailed")
    }

    override fun onBondLost(peripheral: BluetoothPeripheral) {
        println("onBondLost")
    }

    override fun onReadRemoteRssi(peripheral: BluetoothPeripheral, rssi: Int, status: BluetoothCommandStatus) {
        println("onReadRemoteRssi")
    }
}

class BluetoothManager : BluetoothCentralManagerCallback() {

    private val bluetoothCentral: BluetoothCentralManager = BluetoothCentralManager(this)
    private var s4Peripheral: BluetoothPeripheral? = null

    init {
        bluetoothCentral.scanForPeripherals()
    }

    override fun onConnectedPeripheral(peripheral: BluetoothPeripheral) {
        println("onConnectedPeripheral: ${peripheral.name}")
        println("Stopping scan:")
        s4Peripheral = peripheral
        bluetoothCentral.stopScan()
    }

    override fun onConnectionFailed(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
        println("onConnectionFailed: ${peripheral.name} -> ${status.name}")
    }

    override fun onDisconnectedPeripheral(peripheral: BluetoothPeripheral, status: BluetoothCommandStatus) {
        println("onDisconnectedPeripheral: ${peripheral.name}")
        s4Peripheral = null
    }

    override fun onDiscoveredPeripheral(peripheral: BluetoothPeripheral, scanResult: ScanResult) {
        println("onDiscoveredPeripheral: ${peripheral.name} / ${scanResult.name} / ${scanResult.serviceData}::${scanResult.manufacturerData}")
        if (peripheral.name.startsWith("S4", 0)) {
            println("Connecting to ${peripheral.name}....")
            peripheral.connect()
        }
    }

    override fun onScanStarted() {
    }

    override fun onScanStopped() {
    }

    override fun onScanFailed(errorCode: Int) {
    }



}