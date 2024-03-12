package app.demo.ble.di

import app.demo.ble.util.CoroutineDispatcherProvider
import app.demo.ble.util.DefaultCoroutineDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoroutineDispatcherBindingModule {

    @Binds
    fun bindDefaultCoroutineDispatcherProvider(
        impl: DefaultCoroutineDispatcherProvider
    ): CoroutineDispatcherProvider
}