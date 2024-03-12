package app.demo.ble.screen

import app.demo.ble.ble.model.BleDevice

data class DeviceScanUiState(
    val devices: List<BleDevice>,
    val scanEnabled: Boolean,
    val btEnabled: Boolean,
    val locationEnabled: Boolean,
    val permissionState: PermissionState
) {
    companion object Factory {
        fun make(
            devices: List<BleDevice> = emptyList(),
            scanEnabled: Boolean = false,
            btEnabled: Boolean = false,
            locationEnabled: Boolean = false,
            permissionState: PermissionState = PermissionState.NotGranted,
        ): DeviceScanUiState {
            return DeviceScanUiState(
                devices = devices,
                scanEnabled = scanEnabled,
                btEnabled = btEnabled,
                locationEnabled = locationEnabled,
                permissionState = permissionState,
            )
        }
    }
}