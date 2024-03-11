package com.ironbird

import com.welie.blessed.*
import java.util.*

private enum class BluetoothService(val uuid: UUID) {
    DEVICE_INFORMATION(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")),
    GATT_PROFILE(UUID.fromString("00001801-0000-1000-8000-00805f9b34fb")),
    BATTERY(UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE(UUID.fromString("00001826-0000-1000-8000-00805f9b34fb")),
    HEART_RATE(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"));


    companion object {
        fun fromUUID(uuid: UUID): BluetoothService? {
            return entries.firstOrNull { it.uuid == uuid }
        }
    }
}

private enum class BluetoothCharacteristics(val service:BluetoothService, val uuid: UUID) {
    MODEL_NUMBER(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb")),
    SERIAL_NUMBER(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb")),
    FIRMWARE_REVISION(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")),
    HARDWARE_REVISION(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb")),
    SOFTWARE_REVISION(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")),
    MANUFACTURER_NAME(BluetoothService.DEVICE_INFORMATION, UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")),

    BATTERY_LEVEL(BluetoothService.BATTERY, UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")),

    HEART_RATE_MEASUREMENT(BluetoothService.HEART_RATE, UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")),

    ROWER_DATA(BluetoothService.FITNESS_MACHINE, UUID.fromString("00002ad1-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE_STATUS(BluetoothService.FITNESS_MACHINE, UUID.fromString("00002ada-0000-1000-8000-00805f9b34fb"));

    companion object {
        fun fromUUID(uuid: UUID): BluetoothCharacteristics? {
            return entries.firstOrNull { it.uuid == uuid }
        }
    }
}

private enum class BluetoothDescriptors(val characteristics: BluetoothCharacteristics, val uuid: UUID) {
    ;

    companion object {
        fun fromUUID(uuid: UUID): BluetoothDescriptors? {
            return entries.firstOrNull { it.uuid == uuid }
        }
    }

}

private class S4PeripheralCallBack: BluetoothPeripheralCallback() {
    override fun onServicesDiscovered(peripheral: BluetoothPeripheral, services: MutableList<BluetoothGattService>) {

        println("onServicesDiscovered")

        services.forEach { service ->
            val serviceName = BluetoothService.fromUUID(service.uuid)?.name ?: service.uuid
            println("   -> Discovered service $serviceName")

            service.characteristics.forEach { characteristic ->
                val btCharacteristic = BluetoothCharacteristics.fromUUID(characteristic.uuid)
                val characteristicName = btCharacteristic?.name ?: characteristic.uuid
                println("      > Service $serviceName has characteristic $characteristicName... Reading...")

                characteristic.descriptors.forEach { descriptor ->
                    val descriptorName = BluetoothDescriptors.fromUUID(descriptor.uuid)?.name ?: descriptor.uuid
                    println("         > Characteristic $characteristicName has descriptor $descriptorName")
                    peripheral.readDescriptor(descriptor)
                }
                peripheral.readCharacteristic(service.uuid, characteristic.uuid)
            }
        }

        // Subscribing to the things we need
        peripheral.setNotify(BluetoothService.HEART_RATE.uuid, BluetoothCharacteristics.HEART_RATE_MEASUREMENT.uuid, true)
        peripheral.setNotify(BluetoothService.FITNESS_MACHINE.uuid, BluetoothCharacteristics.ROWER_DATA.uuid, true)
    }

    override fun onNotificationStateUpdate(
        peripheral: BluetoothPeripheral,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        val characteristicName = BluetoothCharacteristics.fromUUID(characteristic.uuid)?.name ?: characteristic.uuid
        println("onNotificationStateUpdate [$characteristicName] = ${status.name}")
    }

    override fun onCharacteristicUpdate(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        val serviceName = if (characteristic.service != null)
            BluetoothService.fromUUID(characteristic.service!!.uuid)?.name ?: characteristic.uuid
        else
            "NO_SERVICE"

        val characteristicName = BluetoothCharacteristics.fromUUID(characteristic.uuid)?.name ?: characteristic.uuid
        val parser = BluetoothBytesParser(value)
        println("onCharacteristicUpdate ${peripheral.name}::$serviceName::$characteristicName = ${parser.getStringValue(0)}")
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
        val parser = BluetoothBytesParser(value)
        println("onDescriptorRead ${descriptor.uuid}::${parser.getStringValue(0)}")
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
        val connectedS4s = bluetoothCentral.connectedPeripherals.firstOrNull { it.name.startsWith("S4") }
        if (connectedS4s!= null) {
            println("Cancelling existing S4 connection...")
            bluetoothCentral.cancelConnection(connectedS4s)
        }

        println("Starting scan")
        bluetoothCentral.scanForPeripheralsWithNames(arrayOf("S4 Comms 95"))
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
        if (peripheral.name.startsWith("S4", 0)) {
            println("onDiscoveredPeripheral: ${peripheral.name} / ${scanResult.name} / sdata=${scanResult.serviceData} mdata=${scanResult.manufacturerData}")
            println("Connecting to ${peripheral.name}....")
            println("Is Connected ${bluetoothCentral.connectedPeripherals.any { it.name == peripheral.name }}")
            bluetoothCentral.connectPeripheral(peripheral, S4PeripheralCallBack())
        }
    }

    override fun onScanStarted() {
    }

    override fun onScanStopped() {
    }

    override fun onScanFailed(errorCode: Int) {
    }

    fun disconnect() {
        bluetoothCentral.connectedPeripherals.forEach { peripheral ->
            bluetoothCentral.cancelConnection(peripheral)
        }
    }



}