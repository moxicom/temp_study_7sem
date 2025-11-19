package com.aemarkov.lab2.di

import com.aemarkov.lab2.data.api.ExpenseApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    // Для эмулятора используйте: "http://10.0.2.2:8080/api/"
    // Для реального устройства используйте IP адрес вашего компьютера: "http://192.168.x.x:8080/api/"
    private const val BASE_URL = "http://localhost:8080/api/"
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideExpenseApi(retrofit: Retrofit): ExpenseApi {
        return retrofit.create(ExpenseApi::class.java)
    }
}

