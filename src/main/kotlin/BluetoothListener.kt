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
        TODO("Not yet implemented")
    }

    override fun didConnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Peripheral did connect: ${bluetoothPeripheral.name} / ${bluetoothPeripheral.name}")
    }

    override fun didDisconnect(bluetoothPeripheral: BluetoothPeripheral) {
        println("Peripheral did disconnect: ${bluetoothPeripheral.name}")
        //devices.remove(bluetoothPeripheral)
    }

    override fun didDiscoverCharacteristics(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did discover characteristic from: ${bluetoothPeripheral.name}")
        bluetoothPeripheral.services.forEach { service ->
            println("  > Reading for service ${service.name}")
            service.characteristics.forEach {
                blueFalcon.readCharacteristic(bluetoothPeripheral, it)
                println("    > Reading characteristic ${service.name} / ${it.name}")
            }
        }
    }

    override fun didDiscoverDevice(
        bluetoothPeripheral: BluetoothPeripheral,
        advertisementData: Map<AdvertisementDataRetrievalKeys, Any>
    ) {
        var message = "Did discover device: ${bluetoothPeripheral.name}:\n"
        advertisementData.forEach {
            message += "   ${it.key} -> ${it.value}\n"
        }
        println(message)

//        devices.add(bluetoothPeripheral)
//        println("Connecting to device...")
//        blueFalcon.connect(bluetoothPeripheral, autoConnect = true)
//        println("Connected to device")
    }

    override fun didDiscoverServices(bluetoothPeripheral: BluetoothPeripheral) {
        println("Did discover services from: ${bluetoothPeripheral.name} -> " +
                bluetoothPeripheral.services.joinToString(", ")
        )
    }

    override fun didReadDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        TODO("Not yet implemented")
    }

    override fun didRssiUpdate(bluetoothPeripheral: BluetoothPeripheral) {
        TODO("Not yet implemented")
    }

    override fun didUpdateMTU(bluetoothPeripheral: BluetoothPeripheral) {
        TODO("Not yet implemented")
    }

    override fun didWriteCharacteristic(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristic: BluetoothCharacteristic,
        success: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun didWriteDescriptor(
        bluetoothPeripheral: BluetoothPeripheral,
        bluetoothCharacteristicDescriptor: BluetoothCharacteristicDescriptor
    ) {
        TODO("Not yet implemented")
    }
}