package app.demo.ble.screen

import app.demo.ble.network.model.Device

data class DeviceListUiState(
    val devices: List<Device>,
    val loadingState: LoadingState,
    val errorState: ErrorState
) {
    companion object Factory {
        fun make(
            devices: List<Device> = emptyList(),
            loadingState: LoadingState = LoadingState.None,
            errorState: ErrorState = ErrorState.None
        ): DeviceListUiState {
            return DeviceListUiState(
                devices = devices,
                loadingState = loadingState,
                errorState = errorState
            )
        }
    }
}