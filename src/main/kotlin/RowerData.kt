package com.ironbird

import com.welie.blessed.BluetoothBytesParser

/**
 * Data received from a rower over bluetooth.
 * https://github.com/oesmith/gatt-xml/blob/master/org.bluetooth.characteristic.rower_data.xml
 */

class RowerData(private val bytes: ByteArray){

    val moreData: Boolean
    val averageStrokePresent: Boolean
    val totalDistancePresent: Boolean
    val instantaneousPacePresent: Boolean
    val averagePacePresent: Boolean
    val instantaneousPowerPresent: Boolean
    val averagePowerPresent: Boolean
    val resistanceLevelPresent: Boolean
    val expendedEnergyPresent: Boolean
    val heartRatePresent: Boolean
    val metabolicEquivalentPresent: Boolean
    val elapsedTimePresent: Boolean
    val remainingTimePresent: Boolean

    val strokeCount: Int?
    val strokeRate: Float?
    val averageStrokeRate: Float?
    val totalDistance: Int?
    val instantaneousPace: Int?
    val averagePace: Int?
    val instantaneousPower: Int?
    val averagePower: Int?
    val resistanceLevel: Int?
    val totalEnergy: Int?
    val energyPerHour: Int?
    val energyPerMinute: Int?
    val heartRate: Int?
    val metabolicEquivalent: Float?
    val elapsedTime: Int?
    val remainingTime: Int?
    init {
        val parser = BluetoothBytesParser(bytes)

        // Parse the flags
        val flags = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
        moreData = (flags and 1) > 0
        averageStrokePresent = (flags and 2) > 0
        totalDistancePresent = (flags and 4) > 0
        instantaneousPacePresent = (flags and 8) > 0
        averagePacePresent = (flags and 16) > 0
        instantaneousPowerPresent = (flags and 32) > 0
        averagePowerPresent = (flags and 64) > 0
        resistanceLevelPresent = (flags and 128) > 0
        expendedEnergyPresent = (flags and 256) > 0
        heartRatePresent = (flags and 215) > 0
        metabolicEquivalentPresent = (flags and 1024) > 0
        elapsedTimePresent = (flags and 2048) > 0
        remainingTimePresent = (flags and 4096) > 0

        if (!moreData) {
            strokeRate = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8) / 2f
            strokeCount = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
        } else {
            strokeRate = null
            strokeCount = null
        }
        averageStrokeRate = if (averageStrokePresent)  parser.getFloatValue(BluetoothBytesParser.FORMAT_UINT8) / 2f else null

        totalDistance = if (totalDistancePresent) {
            // Distance is 24 bits integer, reading as a UINT8 + UINT16 shifted 8 bits left
            val ui8 = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
            val ui16 = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
            ui8 + (ui16 shl 8)
        }
        else
            null

        instantaneousPace = if (instantaneousPacePresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16) else null
        averagePace = if (averagePacePresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16) else null
        instantaneousPower = if (instantaneousPowerPresent) parser.getIntValue(BluetoothBytesParser.FORMAT_SINT16) else null
        averagePower = if (averagePowerPresent) parser.getIntValue(BluetoothBytesParser.FORMAT_SINT16) else null
        resistanceLevel = if (resistanceLevelPresent) parser.getIntValue(BluetoothBytesParser.FORMAT_SINT16) else null

        if (expendedEnergyPresent) {
            totalEnergy = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
            energyPerHour = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16)
            energyPerMinute = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8)
        } else {
            totalEnergy = null
            energyPerHour = null
            energyPerMinute = null
        }

        heartRate = if (heartRatePresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8) else null
        metabolicEquivalent = if (metabolicEquivalentPresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8) / 10f else null
        elapsedTime = if (elapsedTimePresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16) else null
        remainingTime = if (remainingTimePresent) parser.getIntValue(BluetoothBytesParser.FORMAT_UINT16) else null
    }

    override fun toString(): String {
        return "RowerData:\n" +
                " strokeCount:         $strokeCount\n" +
                " strokeRate:          $strokeRate\n" +
                " averageStrokeRate:   $averageStrokeRate\n" +
                " totalDistance:       $totalDistance\n" +
                " instantaneousPace:   $instantaneousPace\n" +
                " averagePace:         $averagePace\n" +
                " instantaneousPower:  $instantaneousPower\n" +
                " averagePower:        $averagePower\n" +
                " resistanceLevel:     $resistanceLevel\n" +
                " totalEnergy:         $totalEnergy\n" +
                " energyPerHour:       $energyPerHour\n" +
                " energyPerMinute:     $energyPerMinute"
    }


}