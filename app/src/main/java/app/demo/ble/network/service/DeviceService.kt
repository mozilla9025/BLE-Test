package app.demo.ble.network.service

import app.demo.ble.network.model.DeviceList
import arrow.core.Either
import arrow.retrofit.adapter.either.networkhandling.CallError
import retrofit2.http.GET

interface DeviceService {

    @GET("test/devices")
    suspend fun getDevices(): Either<CallError, DeviceList>
}