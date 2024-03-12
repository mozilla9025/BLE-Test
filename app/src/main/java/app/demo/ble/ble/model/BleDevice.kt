package app.demo.ble.ble.model

data class BleDevice(
    val name: String?,
    val macAddress: String,
    val connectionState: ConnectionState,
    val services: List<BleService>
) {
    enum class ConnectionState {
        Connected, Connecting, Disconnected
    }
}

data class BleService(
    val uuid: String,
    val characteristics: List<BleCharacteristic>
)

data class BleCharacteristic(
    val uuid: String
)