package app.demo.ble.screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.demo.ble.domain.repository.DeviceRepository
import app.demo.ble.util.CoroutineDispatcherProvider
import app.demo.ble.util.mutate
import arrow.retrofit.adapter.either.networkhandling.CallError
import arrow.retrofit.adapter.either.networkhandling.HttpError
import arrow.retrofit.adapter.either.networkhandling.IOError
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    private val _uiState = mutableStateOf(DeviceListUiState.make())
    val uiState: State<DeviceListUiState> = _uiState

    init {
        getDevices()
    }

    fun getDevices() {
        _uiState.mutate { copy(loadingState = LoadingState.Refreshing) }

        viewModelScope.launch {
            withContext(coroutineDispatcherProvider.io) {
                deviceRepository.getDevices()
            }
                .onLeft {
                    _uiState.mutate {
                        copy(
                            loadingState = LoadingState.None,
                            errorState = ErrorState.Error(mapError(it))
                        )
                    }
                }
                .onRight {
                    _uiState.mutate {
                        copy(
                            devices = it.devices,
                            loadingState = LoadingState.None,
                            errorState = ErrorState.None
                        )
                    }
                }
        }
    }

    private fun mapError(callError: CallError): String {
        return when (callError) {
            is HttpError -> callError.message
            is IOError -> callError.cause.localizedMessage ?: ""
            is UnexpectedCallError -> callError.cause.localizedMessage ?: ""
        }
    }
}