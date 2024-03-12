package app.demo.ble.domain.repository

import app.demo.ble.network.model.Device
import app.demo.ble.network.model.DeviceList

fun deviceList(devices: List<Device> = listOf(device())): DeviceList {
    return DeviceList(devices = devices)
}

fun device(
    macAddress: String = "macAddress",
    model: String = "model",
    product: String? = "product",
    firmwareVersion: String = "firmwareVersion",
    serial: String? = "serial",
    installationMode: String? = "installationMode",
    brakeLight: Boolean = false,
    lightMode: String? = "lightMode",
    lightAuto: Boolean = false,
    lightValue: Int = 0,
): Device {
    return Device(
        macAddress = macAddress,
        model = model,
        product = product,
        firmwareVersion = firmwareVersion,
        serial = serial,
        installationMode = installationMode,
        brakeLight = brakeLight,
        lightMode = lightMode,
        lightAuto = lightAuto,
        lightValue = lightValue,
    )
}
