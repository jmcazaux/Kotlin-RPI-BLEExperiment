@file:JvmName("Main")

package com.ironbird

fun main(args: Array<String>) {
    println("Hello World!")

    var bluetoothManager : BluetoothManager? = null

    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() {
            println("Going down! I'm hit! Going down!")
            println("Disconnecting BLE devices")
            bluetoothManager?.disconnect() ?: return
        }
    })

    bluetoothManager = BluetoothManager()
    while (true) {
        try {
            Thread.sleep(1000L)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



}
