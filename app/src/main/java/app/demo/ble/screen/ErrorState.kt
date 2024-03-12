package app.demo.ble.screen

sealed interface ErrorState {
    class Error(val description: String) : ErrorState
    data object None : ErrorState
}