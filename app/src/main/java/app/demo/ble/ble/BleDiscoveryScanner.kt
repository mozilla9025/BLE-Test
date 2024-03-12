package app.demo.ble.ble

import android.bluetooth.BluetoothDevice
import kotlinx.coroutines.flow.Flow

interface BleDiscoveryScanner {
    val scanResults: Flow<Set<BluetoothDevice>>

    fun startScan()

    fun stopScan()
}