package app.demo.ble.domain.repository

import app.demo.ble.network.model.DeviceList
import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError

interface DeviceRepository {

    suspend fun getDevices(): Either<CallError, DeviceList>
}