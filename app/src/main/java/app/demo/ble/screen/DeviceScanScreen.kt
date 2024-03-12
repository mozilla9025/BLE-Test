package app.demo.ble.screen

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.demo.ble.R.string.scan_results_action_enable_bluetooth
import app.demo.ble.R.string.scan_results_action_enable_location
import app.demo.ble.R.string.scan_results_action_enable_scan
import app.demo.ble.R.string.scan_results_connection_action_connect
import app.demo.ble.R.string.scan_results_connection_action_connecting
import app.demo.ble.R.string.scan_results_connection_action_disconnect
import app.demo.ble.R.string.scan_results_device_name_unknown
import app.demo.ble.R.string.scan_results_error_bluetooth_disabled
import app.demo.ble.R.string.scan_results_error_location_disabled
import app.demo.ble.R.string.scan_results_item_characteristic
import app.demo.ble.R.string.scan_results_item_service
import app.demo.ble.ble.model.BleCharacteristic
import app.demo.ble.ble.model.BleDevice
import app.demo.ble.ble.model.BleService
import app.demo.ble.ui.theme.BLEDemoTheme
import app.demo.ble.ui.theme.Spacings
import app.demo.ble.util.blePermissions
import java.util.UUID

@Composable
fun DeviceScanScreen(
    viewModel: DeviceScanViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { granted: Map<String, @JvmSuppressWildcards Boolean> ->
        viewModel.processPermissions(granted)
    }
    val btLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshState()
    }
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshState()
    }

    LaunchedEffect(uiState.permissionState) {
        if (!uiState.btEnabled) return@LaunchedEffect

        when (uiState.permissionState) {
            PermissionState.Requesting -> permissionLauncher.launch(blePermissions)
            else -> Unit
        }
    }

    LaunchedEffect(uiState.locationEnabled) {
        viewModel.refreshState()
    }

    DeviceScanScreenContent(uiState = uiState,
        toggleSearch = { viewModel.toggleScan(it) },
        enableBluetooth = { btLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)) },
        enableLocation = { locationLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
        toggleDeviceConnection = { viewModel.toggleDeviceConnection(it) }
    )
}

@Composable
private fun DeviceScanScreenContent(
    uiState: DeviceScanUiState,
    toggleSearch: (Boolean) -> Unit,
    enableBluetooth: () -> Unit,
    enableLocation: () -> Unit,
    toggleDeviceConnection: (device: BleDevice) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
    ) {
        if (!uiState.btEnabled) {
            ErrorView(
                text = stringResource(id = scan_results_error_bluetooth_disabled),
                buttonActionText = stringResource(id = scan_results_action_enable_bluetooth),
                buttonAction = enableBluetooth
            )
        } else if (!uiState.locationEnabled) {
            ErrorView(
                text = stringResource(id = scan_results_error_location_disabled),
                buttonActionText = stringResource(id = scan_results_action_enable_location),
                buttonAction = enableLocation
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp)
                    .background(MaterialTheme.colors.background)
                    .padding(horizontal = Spacings.spacingD.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = stringResource(id = scan_results_action_enable_scan))

                Switch(
                    checked = uiState.scanEnabled,
                    onCheckedChange = toggleSearch
                )
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                uiState.devices.forEach {
                    item {
                        DeviceScanViewHolder(it, toggleDeviceConnection)
                    }
                }
            }
        }
    }
}


@Composable
private fun DeviceScanViewHolder(
    device: BleDevice,
    connectDevice: (device: BleDevice) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Spacings.spacingD.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacings.spacingA.dp)
        ) {
            Text(
                text = device.name ?: stringResource(id = scan_results_device_name_unknown),
                style = MaterialTheme.typography.h6
            )
            Text(text = device.macAddress, style = MaterialTheme.typography.subtitle1)

            AnimatedVisibility(
                visible = device.services.isNotEmpty(),
            ) {
                Column {
                    device.services.forEach { service ->
                        Text(
                            text = stringResource(id = scan_results_item_service),
                            style = MaterialTheme.typography.body1
                        )
                        Text(text = service.uuid, style = MaterialTheme.typography.body2)

                        Spacer(modifier = Modifier.height(Spacings.spacingA.dp))

                        Text(
                            text = stringResource(id = scan_results_item_characteristic),
                            style = MaterialTheme.typography.body1
                        )
                        service.characteristics.forEach { characteristic ->
                            Text(text = characteristic.uuid, style = MaterialTheme.typography.body2)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { connectDevice(device) },
            enabled = device.connectionState != BleDevice.ConnectionState.Connecting
        ) {
            val text = when (device.connectionState) {
                BleDevice.ConnectionState.Connected -> stringResource(id = scan_results_connection_action_disconnect)
                BleDevice.ConnectionState.Connecting -> stringResource(id = scan_results_connection_action_connecting)
                BleDevice.ConnectionState.Disconnected -> stringResource(id = scan_results_connection_action_connect)
            }
            Text(text = text)
        }
    }
}

@Composable
@Preview(device = "id:pixel_5")
private fun DeviceListScreenPreview() {
    val uiState = DeviceScanUiState.make(
        devices = listOf(
            BleDevice(
                name = "Test name",
                macAddress = "macAddress",
                connectionState = BleDevice.ConnectionState.Connecting,
                services = listOf(
                    BleService(
                        uuid = UUID.randomUUID().toString(),
                        characteristics = listOf(
                            BleCharacteristic(UUID.randomUUID().toString())
                        )
                    )
                )
            )
        ),
        btEnabled = true
    )

    BLEDemoTheme {
        DeviceScanScreenContent(uiState, {}, {}, {}, {})
    }
}