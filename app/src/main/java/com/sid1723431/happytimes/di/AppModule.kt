package com.sid1723431.happytimes.di

import androidx.room.Room
import android.app.Application
import android.app.SharedElementCallback
import com.sid1723431.happytimes.data.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app : Application,
        callback: HabitDatabase.Callback
    ) =
        Room.databaseBuilder(app, HabitDatabase::class.java, "habit_database")
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()
    @Provides
    fun provideHabitDao(db :HabitDatabase) = db.habitDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun providesApplicationScrope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope