package com.ironbird

import AdvertisementDataRetrievalKeys
import dev.bluefalcon.*

class BluetoothListener: BlueFalconDelegate {

    private val blueFalcon = BlueFalcon(ApplicationContext(), null)
    private val devices = mutableListOf<BluetoothPeripheral>()

    init {
        blueFalcon.delegates.add(this)
        blueFalcon.scan()
    }
    override fun didCharacteristcValueChanged(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic
    ) {
        println("Characteristic value changed: ${bluetoothPeripheral.name} :: ${bluetoothCharacteristic.name}: ${bluetoothCharacteristic.value}")
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Peripheral did connect: ${bluetoothPeripheral.name} / ${bluetoothPeripheral.name}")
        println("Stopping scan...")
        blueFalcon.stopScanning()
    }

    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Peripheral did disconnect: ${bluetoothPeripheral.name}")
        devices.remove(bluetoothPeripheral)

        println("Restarting scan...")
        blueFalcon.scan()
    }

    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did discover characteristic from: ${bluetoothPeripheral.name}. ${bluetoothPeripheral.services.count()} services...")
        bluetoothPeripheral.services.forEach { service ->
            println("  > Reading for service ${service.name}")
            service.characteristics.forEach { characteristic ->
                blueFalcon.readCharacteristic(bluetoothPeripheral, characteristic)
                println("    > Reading characteristic ${service.name} / ${characteristic.name}::${characteristic.value}")
            }
        }
    }

    override fun didDiscoverDevice(
        bluetoothPeripheral: BluetoothPeripheral,
        advertisementData: Map<AdvertisementDataRetrievalKeys, Any>
    ) {

        val peripheralName = bluetoothPeripheral.name ?: return

        if (peripheralName.isEmpty())
            return

        if (peripheralName.startsWith("S4 Comms 95")) {

            var message = "Did discover S4 / ${bluetoothPeripheral.uuid}:\n"
            advertisementData.forEach {
                message += "   ${it.key} -> ${it.value}\n"
            }
            println(message)

            devices.add(bluetoothPeripheral)
            println("Connecting to device...")
            blueFalcon.connect(bluetoothPeripheral, autoConnect = true)
            println("Connected to device. Stopping scan.")
            blueFalcon.stopScanning()
        }
    }

    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did discover services from: ${bluetoothPeripheral.name} -> " +
                bluetoothPeripheral.services.map { "${it.name}" }.joinToString(", ")
        )
        bluetoothPeripheral.services.map { service ->
            service.characteristics.map { characteristic ->
                println("   - characteristic ${characteristic.name}::${characteristic.value}")

                blueFalcon.notifyCharacteristic(bluetoothPeripheral, characteristic, true)
                blueFalcon.readCharacteristic(bluetoothPeripheral, characteristic)
            }
        }
    }

    override fun didReadDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        println("Did read descriptor: ${bluetoothPeripheral.name}")
    }

    override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did Update RSSI from: ${bluetoothPeripheral.name}")
    }

    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did Update MTU from: ${bluetoothPeripheral.name}")
    }

    override fun didWriteCharacteristic(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic,
        success: Boolean
    ) {
        println("Did write characteristic ${bluetoothPeripheral.name} -> ${bluetoothCharacteristic.value}")
    }

    override fun didWriteDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        println("Did write characteristic ${bluetoothPeripheral.name}")
    }
}