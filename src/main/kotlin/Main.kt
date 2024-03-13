@file:JvmName("Main")

package com.ironbird


fun main(args: Array<String>) {
    println("Hello World!")

//    val rowerData = RowerData(byteArrayOf(44, 11, 52, 12, 0, 98, 0, 0, -1, -1, 0, 0, 3, 0, -18, 5, 25, 0, 104, 1))
//    println(rowerData.toString())

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
