package app.demo.ble.screen

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.demo.ble.ble.BleDeviceManager
import app.demo.ble.ble.BleDiscoveryScanner
import app.demo.ble.ble.model.BleDevice
import app.demo.ble.util.blePermissions
import app.demo.ble.util.mutate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class DeviceScanViewModel @Inject constructor(
    private val bleDiscoveryScanner: BleDiscoveryScanner,
    private val bleDeviceManager: BleDeviceManager,
    @ApplicationContext
    private val context: Context
) : ViewModel() {

    private val _uiState = mutableStateOf(DeviceScanUiState.make())
    val uiState: State<DeviceScanUiState> = _uiState

    init {
        observeScanResults()

        _uiState.mutate {
            this.copy(
                btEnabled = bleDeviceManager.isBtEnabled(),
                locationEnabled = isLocationEnabled(),
            )
        }
    }

    fun toggleScan(scanEnabled: Boolean) {
        if (!scanEnabled) {
            stopScan()
            _uiState.mutate { copy(scanEnabled = false) }
            return
        }

        if (isPermissionGranted()) {
            _uiState.mutate {
                copy(
                    scanEnabled = true,
                    permissionState = PermissionState.Granted
                )
            }
            startScan()
        } else {
            _uiState.mutate {
                copy(
                    scanEnabled = false,
                    permissionState = PermissionState.Requesting
                )
            }
        }
    }

    fun refreshState() {
        _uiState.mutate {
            copy(
                btEnabled = bleDeviceManager.isBtEnabled(),
                locationEnabled = isLocationEnabled()
            )
        }
    }

    fun processPermissions(granted: Map<String, @JvmSuppressWildcards Boolean>) {
        if (granted.all { (_, isGranted) -> isGranted }) {
            _uiState.mutate { copy(permissionState = PermissionState.Granted) }
        } else {
            _uiState.mutate {
                copy(
                    scanEnabled = false,
                    permissionState = PermissionState.NotGranted
                )
            }
        }
    }

    fun toggleDeviceConnection(device: BleDevice) {
        viewModelScope.launch {
            when (device.connectionState) {
                BleDevice.ConnectionState.Connected -> bleDeviceManager.disconnect(device.macAddress)
                BleDevice.ConnectionState.Disconnected -> {
                    stopScan()
                    _uiState.mutate { copy(scanEnabled = false) }
                    bleDeviceManager.connect(device.macAddress)
                }

                BleDevice.ConnectionState.Connecting -> Unit
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return blePermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun observeScanResults() {
        viewModelScope.launch {
            bleDiscoveryScanner.scanResults
                .combine(bleDeviceManager.deviceState) { scanResults, device ->
                    mapScanResultsWithDevices(scanResults, device)
                }.collect { devices ->
                    _uiState.mutate { copy(devices = devices) }
                }
        }
    }

    private fun startScan() {
        bleDiscoveryScanner.startScan()
    }

    private fun stopScan() {
        bleDiscoveryScanner.stopScan()
    }

    private fun isLocationEnabled(): Boolean {
        return context.getSystemService<LocationManager>()?.let {
            LocationManagerCompat.isLocationEnabled(it)
        } ?: false
    }

    @SuppressLint("MissingPermission")
    private fun mapScanResultsWithDevices(
        scanResults: Set<BluetoothDevice>,
        device: BleDevice?
    ): List<BleDevice> {
        return scanResults.map {
            if (device != null && device.macAddress == it.address) {
                device
            } else {
                BleDevice(
                    name = it.name ?: "Unknown",
                    macAddress = it.address,
                    connectionState = BleDevice.ConnectionState.Disconnected,
                    services = emptyList()
                )
            }
        }
    }
}