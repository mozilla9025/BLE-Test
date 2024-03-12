package app.demo.ble.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

interface CoroutineDispatcherProvider {
    val io: CoroutineDispatcher
    val ui: CoroutineDispatcher
    val computation: CoroutineDispatcher
}

class DefaultCoroutineDispatcherProvider @Inject constructor() :
    CoroutineDispatcherProvider {

    override val io: CoroutineDispatcher = Dispatchers.IO
    override val ui: CoroutineDispatcher = Dispatchers.Main
    override val computation: CoroutineDispatcher = Dispatchers.Default
}