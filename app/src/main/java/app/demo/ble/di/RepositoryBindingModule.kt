package app.demo.ble.di

import app.demo.ble.domain.repository.DeviceRepository
import app.demo.ble.domain.repository.impl.DeviceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryBindingModule {

    @Binds
    fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository
}