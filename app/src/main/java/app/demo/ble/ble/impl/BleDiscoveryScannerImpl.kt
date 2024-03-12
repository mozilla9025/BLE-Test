package app.demo.ble.ble.impl

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import app.demo.ble.ble.BleCoroutineScope
import app.demo.ble.ble.BleDiscoveryScanner
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleDiscoveryScannerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    @BleCoroutineScope
    private val coroutineScope: CoroutineScope,
    private val adapter: BluetoothAdapter?
) : BleDiscoveryScanner {

    private val _scanResults = MutableStateFlow<Set<BluetoothDevice>>(emptySet())

    override val scanResults: Flow<Set<BluetoothDevice>> = _scanResults.asStateFlow()

    override fun startScan() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(scanReceiver, filter)

        if (adapter?.isDiscovering == true) {
            adapter.cancelDiscovery()
        }
        adapter?.startDiscovery()
    }

    override fun stopScan() {
        if (adapter?.isDiscovering == false) return

        adapter?.cancelDiscovery()
        context.unregisterReceiver(scanReceiver)
    }

    private val scanReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                coroutineScope.launch {
                    val action = intent?.action
                    when (action) {
                        BluetoothDevice.ACTION_FOUND -> {
                            val device =
                                intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                                    ?: return@launch

                            val current = _scanResults.value
                            val updated = current.toMutableSet()
                            updated.add(device)
                            _scanResults.emit(updated)
                        }
                    }
                }
            }
        }
    }
}