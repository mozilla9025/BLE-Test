package app.demo.ble.domain.repository

import app.demo.ble.domain.repository.impl.DeviceRepositoryImpl
import app.demo.ble.network.service.DeviceService
import arrow.core.left
import arrow.core.right
import arrow.retrofit.adapter.either.networkhandling.UnexpectedCallError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceRepositoryImplTest {

    private val deviceService = mockk<DeviceService>(relaxed = true)
    private val repository = DeviceRepositoryImpl(deviceService)

    @Test
    fun `getDevices should return devices when success`() = runTest {
        // Given
        val expectedDeviceList = deviceList()
        coEvery { deviceService.getDevices() } returns expectedDeviceList.right()

        // When
        val result = repository.getDevices()

        // Then
        assertTrue(result.isRight())
        assertNotNull(result.getOrNull())
        assertEquals(expectedDeviceList, result.getOrNull())
        coVerify { deviceService.getDevices() }
    }

    @Test
    fun `getDevices should return CallError when fail`() = runTest {
        // Given
        val expectedError = UnexpectedCallError(Throwable())
        coEvery { deviceService.getDevices() } returns expectedError.left()

        // When
        val result = repository.getDevices()

        // Then
        assertTrue(result.isLeft())
        assertNull(result.getOrNull())
        assertEquals(expectedError, result.leftOrNull())
        coVerify { deviceService.getDevices() }
    }
}