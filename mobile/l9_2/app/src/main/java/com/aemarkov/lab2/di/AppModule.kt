package com.aemarkov.lab2.di

import android.content.Context
import com.aemarkov.lab2.data.local.PreferencesHelper
import com.aemarkov.lab2.data.repository.ExpenseRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun providePreferencesHelper(
        @ApplicationContext context: Context,
        gson: Gson
    ): PreferencesHelper {
        return PreferencesHelper(context, gson)
    }
    
    @Provides
    @Singleton
    fun provideExpenseRepository(
        api: com.aemarkov.lab2.data.api.ExpenseApi,
        preferencesHelper: PreferencesHelper
    ): ExpenseRepository {
        return ExpenseRepository(api, preferencesHelper)
    }
    
    @Provides
    @Singleton
    fun provideNotificationHelper(
        @ApplicationContext context: Context
    ): com.aemarkov.lab2.util.NotificationHelper {
        return com.aemarkov.lab2.util.NotificationHelper(context)
    }
}

