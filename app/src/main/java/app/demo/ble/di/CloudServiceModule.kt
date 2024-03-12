package app.demo.ble.di

import app.demo.ble.network.service.DeviceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class CloudServiceModule {

    @Provides
    fun provideDeviceService(retrofit: Retrofit): DeviceService {
        return retrofit.create(DeviceService::class.java)
    }
}