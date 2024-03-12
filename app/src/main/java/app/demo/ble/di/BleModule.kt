package app.demo.ble.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.core.content.getSystemService
import app.demo.ble.ble.BleCoroutineScope
import app.demo.ble.util.CoroutineDispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
class BleModule {

    @Provides
    fun provideBluetoothManager(@ApplicationContext context: Context): BluetoothManager? {
        return context.getSystemService<BluetoothManager>()
    }

    @Provides
    fun provideBluetoothAdapter(bluetoothManager: BluetoothManager?): BluetoothAdapter? {
        return bluetoothManager?.adapter
    }

    @Provides
    @BleCoroutineScope
    fun provideBleCoroutineScope(
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): CoroutineScope {
        return CoroutineScope(coroutineDispatcherProvider.io + SupervisorJob())
    }
}