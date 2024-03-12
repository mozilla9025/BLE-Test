package app.demo.ble.util

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.MutableStateFlow

fun <T> MutableState<T>.mutate(mutator: T.() -> T) {
    val value = this.value
    this.value = value.mutator()
}

suspend fun <T> MutableStateFlow<T>.mutate(mutator: T.() -> T) {
    val value = this.value
    this.emit(value.mutator())
}