package app.demo.ble.ble

import app.demo.ble.ble.model.BleDevice
import kotlinx.coroutines.flow.Flow


interface BleDeviceManager {
    val deviceState: Flow<BleDevice?>

    fun isBtEnabled(): Boolean

    suspend fun connect(macAddress: String)

    suspend fun disconnect(macAddress: String)
}