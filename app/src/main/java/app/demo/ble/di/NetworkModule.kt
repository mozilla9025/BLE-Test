package app.demo.ble.di

import app.demo.ble.BuildConfig
import arrow.retrofit.adapter.either.EitherCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Provider

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    internal fun provideOkHttpClient(
        loggingInterceptorProvider: Provider<HttpLoggingInterceptor>
    ): OkHttpClient {
        val loggingInterceptor = if (BuildConfig.DEBUG) loggingInterceptorProvider.get() else null

        return OkHttpClient.Builder()
            .apply {
                loggingInterceptor?.let {
                    this.addInterceptor(it)
                }

                this.connectTimeout(30, TimeUnit.SECONDS)
                this.readTimeout(30, TimeUnit.SECONDS)
                this.writeTimeout(30, TimeUnit.SECONDS)
            }
            .build()
    }

    @Provides
    internal fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    internal fun provideBaseUrl(): String {
        return BuildConfig.BASE_URL
    }

    @Provides
    internal fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
        eitherCallAdapterFactory: EitherCallAdapterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(moshiConverterFactory)
            .addCallAdapterFactory(eitherCallAdapterFactory)
            .build()
    }
}