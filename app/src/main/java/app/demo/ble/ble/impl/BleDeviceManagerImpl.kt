package app.demo.ble.ble.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import app.demo.ble.ble.BleCoroutineScope
import app.demo.ble.ble.BleDeviceManager
import app.demo.ble.ble.model.BleCharacteristic
import app.demo.ble.ble.model.BleDevice
import app.demo.ble.ble.model.BleService
import app.demo.ble.util.mutate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@SuppressLint("MissingPermission")
class BleDeviceManagerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @BleCoroutineScope
    private val coroutineScope: CoroutineScope,
    private val bluetoothAdapter: BluetoothAdapter?
) : BleDeviceManager {

    private var bluetoothGatt: BluetoothGatt? = null

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (gatt == null || gatt != bluetoothGatt) return

            if (status == BluetoothGatt.GATT_SUCCESS) {
                coroutineScope.launch {
                    _deviceState.mutate {
                        this?.copy(services = mapServices(gatt))
                    }
                }
            }
        }

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (gatt != bluetoothGatt) return

            when (newState) {
                BluetoothProfile.STATE_DISCONNECTED -> {
                    coroutineScope.launch {
                        _deviceState.mutate {
                            this?.copy(connectionState = BleDevice.ConnectionState.Disconnected)
                        }
                    }
                }

                BluetoothProfile.STATE_CONNECTED -> {
                    coroutineScope.launch {
                        _deviceState.mutate {
                            this?.copy(connectionState = BleDevice.ConnectionState.Connected)
                        }
                    }
                    gatt?.discoverServices()
                }
            }
        }
    }

    private val _deviceState = MutableStateFlow<BleDevice?>(null)

    override val deviceState: Flow<BleDevice?> = _deviceState.asStateFlow()

    override fun isBtEnabled(): Boolean {
        return bluetoothAdapter?.isEnabled ?: false
    }

    override suspend fun connect(macAddress: String) {
        val adapter = bluetoothAdapter ?: throw IllegalStateException("Bluetooth adapter is NULL")

        val device = adapter.getRemoteDevice(macAddress)
            ?: throw IllegalStateException("Device not found. Unable to connect.")

        coroutineScope.launch {
            _deviceState.emit(
                BleDevice(
                    name = device.name,
                    macAddress = device.address,
                    connectionState = BleDevice.ConnectionState.Connecting,
                    services = emptyList()
                )
            )

            bluetoothGatt = device.connectGatt(context, false, gattCallback)
        }
    }

    override suspend fun disconnect(macAddress: String) {
        coroutineScope.launch {
            bluetoothGatt?.disconnect()
            _deviceState.emit(null)
        }
    }

    private fun mapServices(gatt: BluetoothGatt): List<BleService> {
        return gatt.services.map { service ->
            BleService(uuid = service.uuid.toString(),
                characteristics = service.characteristics.map {
                    BleCharacteristic(it.uuid.toString())
                }
            )
        }
    }
}