package app.demo.ble.domain.repository.impl

import app.demo.ble.domain.repository.DeviceRepository
import app.demo.ble.network.model.DeviceList
import app.demo.ble.network.service.DeviceService
import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceService: DeviceService
) : DeviceRepository {

    override suspend fun getDevices(): Either<CallError, DeviceList> {
        return deviceService.getDevices()
    }
}