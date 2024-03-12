package app.demo.ble.di

import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class UtilModule {

    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Provides
    internal fun provideMoshiConverterFactory(
        moshi: Moshi
    ): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    internal fun provideEitherCallAdapterFactory(): EitherCallAdapterFactory {
        return EitherCallAdapterFactory.create()
    }
}