@file:OptIn(ExperimentalMaterialApi::class)

package app.demo.ble.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.demo.ble.network.model.Device
import app.demo.ble.ui.theme.BLEDemoTheme
import app.demo.ble.ui.theme.Spacings

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val pullToRefreshState = rememberPullRefreshState(
        refreshing = uiState.loadingState == LoadingState.Refreshing,
        onRefresh = { viewModel.getDevices() }
    )
    DeviceListScreenContent(pullToRefreshState, uiState)
}

@Composable
private fun DeviceListScreenContent(
    pullToRefreshState: PullRefreshState,
    uiState: DeviceListUiState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .pullRefresh(pullToRefreshState),
        contentAlignment = Alignment.TopStart
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            uiState.devices.forEachIndexed { index, device ->
                item {
                    DeviceItemViewHolder(device, {})

                    if (index < uiState.devices.lastIndex) {
                        Divider()
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = uiState.loadingState == LoadingState.Refreshing,
            state = pullToRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun DeviceItemViewHolder(
    device: Device,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(Spacings.spacingD.dp),
        verticalArrangement = Arrangement.spacedBy(Spacings.spacingA.dp)
    ) {
        Text(text = device.model, style = MaterialTheme.typography.h6)
        Text(text = device.macAddress, style = MaterialTheme.typography.body2)
    }
}

@Composable
@Preview(device = "id:pixel_5")
private fun DeviceListScreenPreview() {
    val pullToRefreshState = rememberPullRefreshState(refreshing = true, onRefresh = {})
    val uiState = DeviceListUiState.make(
        loadingState = LoadingState.Refreshing,
        devices = listOf(
            Device(
                macAddress = "macAddress",
                model = "model",
                product = "product",
                firmwareVersion = "firmwareVersion",
                serial = "serial",
                installationMode = "installationMode",
                brakeLight = false,
                lightMode = "lightMode",
                lightAuto = false,
                lightValue = 0,
            )
        )
    )

    BLEDemoTheme {
        DeviceListScreenContent(pullToRefreshState, uiState)
    }
}