package com.ironbird

import com.welie.blessed.*
import java.util.*

//https://gist.github.com/sam016/4abe921b5a9ee27f67b3686910293026

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

        fun nameFromUUID(uuid: UUID): String {
            return BluetoothService.fromUUID(uuid)?.name ?: uuid.toString()
        }

    }
}

private enum class BluetoothCharacteristics(val uuid: UUID) {
    MODEL_NUMBER(UUID.fromString("00002a24-0000-1000-8000-00805f9b34fb")),
    SERIAL_NUMBER(UUID.fromString("00002a25-0000-1000-8000-00805f9b34fb")),
    FIRMWARE_REVISION(UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")),
    HARDWARE_REVISION(UUID.fromString("00002a27-0000-1000-8000-00805f9b34fb")),
    SOFTWARE_REVISION(UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")),
    MANUFACTURER_NAME(UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb")),
    SYSTEM_ID(UUID.fromString("00002a23-0000-1000-8000-00805f9b34fb")),
    IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST(UUID.fromString("00002a2a-0000-1000-8000-00805f9b34fb")),

    BATTERY_LEVEL(UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")),

    HEART_RATE_MEASUREMENT(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")),

    ROWER_DATA(UUID.fromString("00002ad1-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE_STATUS(UUID.fromString("00002ada-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE_FEATURE(UUID.fromString("00002acc-0000-1000-8000-00805f9b34fb")),
    FITNESS_MACHINE_CONTROL_POINT(UUID.fromString("00002ad9-0000-1000-8000-00805f9b34fb"));



    companion object {
        fun fromUUID(uuid: UUID): BluetoothCharacteristics? {
            return entries.firstOrNull { it.uuid == uuid }
        }

        fun nameFromUUID(uuid: UUID): String {
            return BluetoothCharacteristics.fromUUID(uuid)?.name ?: uuid.toString()
        }

    }
}

private enum class BluetoothDescriptors(val uuid: UUID) {

    CHARACTERISTIC_EXTENDED_PROPERTIES(UUID.fromString("00002900-0000-1000-8000-00805f9b34fb")),
    CHARACTERISTIC_USER_DESCRIPTION(UUID.fromString("00002901-0000-1000-8000-00805f9b34fb")),
    CLIENT_CHARACTERISTIC_CONFIGURATION(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")),
    SERVER_CHARACTERISTIC_CONFIGURATION(UUID.fromString("00002903-0000-1000-8000-00805f9b34fb")),
    CHARACTERISTIC_PRESENTATION_FORMAT(UUID.fromString("00002904-0000-1000-8000-00805f9b34fb")),
    CHARACTERISTIC_AGGREGATE_FORMAT(UUID.fromString("00002905-0000-1000-8000-00805f9b34fb")),
    VALID_RANGE(UUID.fromString("00002906-0000-1000-8000-00805f9b34fb")),
    EXTERNAL_REPORT_REFERENCE(UUID.fromString("00002907-0000-1000-8000-00805f9b34fb")),
    REPORT_REFERENCE(UUID.fromString("00002908-0000-1000-8000-00805f9b34fb")),
    NUMBER_OF_DIGITALS(UUID.fromString("00002909-0000-1000-8000-00805f9b34fb")),
    VALUE_TRIGGER_SETTING(UUID.fromString("0000290a-0000-1000-8000-00805f9b34fb")),
    ENVIRONMENTAL_SENSING_CONFIGURATION(UUID.fromString("0000290b-0000-1000-8000-00805f9b34fb")),
    ENVIRONMENTAL_SENSING_MEASUREMENT(UUID.fromString("0000290c-0000-1000-8000-00805f9b34fb")),
    ENVIRONMENTAL_SENSING_TRIGGER_SETTING(UUID.fromString("0000290d-0000-1000-8000-00805f9b34fb")),
    TIME_TRIGGER_SETTING(UUID.fromString("0000290e-0000-1000-8000-00805f9b34fb"))
    ;

    companion object {
        fun fromUUID(uuid: UUID): BluetoothDescriptors? {
            return entries.firstOrNull { it.uuid == uuid }
        }

        fun nameFromUUID(uuid: UUID): String {
            return fromUUID(uuid)?.name ?: uuid.toString()
        }
    }

}

private class S4PeripheralCallBack: BluetoothPeripheralCallback() {
    override fun onServicesDiscovered(peripheral: BluetoothPeripheral, services: MutableList<BluetoothGattService>) {

        println("onServicesDiscovered")

        services.forEach { service ->
            val serviceName = BluetoothService.nameFromUUID(service.uuid)
            println("   -> Discovered service $serviceName")

            service.characteristics.forEach { characteristic ->

                val characteristicName = BluetoothCharacteristics.nameFromUUID(characteristic.uuid)
                println("      > Service $serviceName has characteristic $characteristicName... Reading...")

                characteristic.descriptors.forEach { descriptor ->
                    val descriptorName = BluetoothDescriptors.nameFromUUID(descriptor.uuid)
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
        val characteristicName = BluetoothCharacteristics.nameFromUUID(characteristic.uuid)
        println("onNotificationStateUpdate [$characteristicName] = ${status.name}")
    }

    override fun onCharacteristicUpdate(
        peripheral: BluetoothPeripheral,
        value: ByteArray,
        characteristic: BluetoothGattCharacteristic,
        status: BluetoothCommandStatus
    ) {
        val serviceName = if (characteristic.service != null)
            BluetoothService.nameFromUUID(characteristic.service!!.uuid)
        else
            "NO_SERVICE"

        if (characteristic.uuid == BluetoothCharacteristics.ROWER_DATA.uuid) {
            val rowerData = RowerData(value)
            println(
                "| ${rowerData.strokeCount ?: "NO strokeCount"} | " +
                "${rowerData.strokeRate ?: "NO strokeRate"} | " +
                //"${rowerData.averageStrokeRate ?: "NO averageStrokeRate"} | " +
                "${rowerData.totalDistance ?: "NO totalDistance"} | " +
                "${rowerData.instantaneousPace ?: "NO instantaneousPace"} | " +
                //"${rowerData.averagePace ?: "NO averagePace"} | " +
                "${rowerData.instantaneousPower ?: "NO instantaneousPower"} |" +
                //" ${rowerData.averagePower ?: "NO averagePower"} | " +
                //"${rowerData.resistanceLevel ?: "NO resistanceLevel"} | " +
                "${rowerData.totalEnergy ?: "NO totalEnergy"} | " +
                "${rowerData.energyPerHour ?: "NO energyPerHour"} | " +
                "${rowerData.energyPerMinute ?: "NO energyPerMinute"} | " +
                "${rowerData.heartRate ?: "NO heartRate"} | " +
                //"${rowerData.metabolicEquivalent ?: "NO metabolicEquivalent"} | " +
                "${rowerData.elapsedTime ?: "NO elapsedTime"} | "
                //"${rowerData.remainingTime ?: "NO remainingTime"} |"
            )
        } else if (characteristic.uuid != BluetoothCharacteristics.HEART_RATE_MEASUREMENT.uuid) {
            val characteristicName = BluetoothCharacteristics.nameFromUUID(characteristic.uuid)
            val parser = BluetoothBytesParser(value)
            println("onCharacteristicUpdate ${peripheral.name}::$serviceName::$characteristicName = ${parser.getStringValue(0)}::${value.contentToString()}")
        }
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
        println("onDescriptorRead ${BluetoothDescriptors.nameFromUUID(descriptor.uuid)}::${value.contentToString()}")
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
        bluetoothCentral.stopScan()
        bluetoothCentral.connectedPeripherals.forEach { peripheral ->
            bluetoothCentral.cancelConnection(peripheral)
        }
        bluetoothCentral.shutdown()
    }



}