package com.example.diplom.data.di

import android.app.Application
import android.content.Context
import com.example.diplom.data.api.*
import com.example.diplom.data.prefs.PreferenceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import javax.net.ssl.*

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    private const val BASE_URL = "https://netomedia.ru/"

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideHeaderInterceptor(preferenceService: PreferenceService): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            preferenceService.accessToken?.let {
                requestBuilder.addHeader("Authorization", it)
            }
            it.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }

    @Provides
    @Singleton
    fun provideMediaService(retrofit: Retrofit): MediaService {
        return retrofit.create(MediaService::class.java)
    }

    @Provides
    @Singleton
    fun provideEventService(retrofit: Retrofit): EventService {
        return retrofit.create(EventService::class.java)
    }

    @Provides
    @Singleton
    fun provideJobService(retrofit: Retrofit): JobService {
        return retrofit.create(JobService::class.java)
    }

    @Provides
    @Singleton
    fun providePostService(retrofit: Retrofit): PostService {
        return retrofit.create(PostService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(client)
        .build()


    @Provides
    @Singleton
    fun provideHostnameVerifier(): HostnameVerifier {
        return HostnameVerifier { hostname, session ->
            HttpsURLConnection.getDefaultHostnameVerifier().run {
                verify(hostname, session)
            }
        }
    }

    @Provides
    @Singleton
    fun provideX509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {}

            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {}

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        }
    }

    @Provides
    @Singleton
    fun provideSSLSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        return SSLContext.getInstance("TLS").apply {
            init(
                null,
                arrayOf<TrustManager>(trustManager),
                java.security.SecureRandom()
            )
        }.socketFactory
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        hostnameVerifier: HostnameVerifier,
        sslSocketFactory: SSLSocketFactory,
        unsafeX509TrustManager: X509TrustManager
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .sslSocketFactory(sslSocketFactory, unsafeX509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .build()
    }
}