package app.demo.ble.di

import app.demo.ble.ble.BleDeviceManager
import app.demo.ble.ble.BleDiscoveryScanner
import app.demo.ble.ble.impl.BleDeviceManagerImpl
import app.demo.ble.ble.impl.BleDiscoveryScannerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface BleBindingModule {

    @Binds
    fun bindBleDeviceManager(impl: BleDeviceManagerImpl): BleDeviceManager

    @Binds
    fun bindBleDiscoveryScanner(impl: BleDiscoveryScannerImpl): BleDiscoveryScanner
}