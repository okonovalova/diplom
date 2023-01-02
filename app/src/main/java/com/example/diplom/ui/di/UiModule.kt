package com.example.diplom.ui.di

import com.example.diplom.ui.utils.BottomMenuListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.net.ssl.*

@InstallIn(SingletonComponent::class)
@Module
object UiModule {

    @Provides
    @Singleton
    fun provideBottomMenuListener(): BottomMenuListener = BottomMenuListener()
}