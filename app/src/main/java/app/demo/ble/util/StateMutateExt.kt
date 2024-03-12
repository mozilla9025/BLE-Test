package app.demo.ble.util

import androidx.compose.runtime.MutableState

fun <T> MutableState<T>.mutate(mutator: T.() -> T) {
    val value = this.value
    this.value = value.mutator()
}