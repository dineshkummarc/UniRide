package com.drdisagree.uniride.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.drdisagree.uniride.data.database.ScheduleDatabase
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.drdisagree.uniride.domain.repository.ScheduleRepository
import com.drdisagree.uniride.domain.repository.ScheduleRepositoryImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage.reference

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideScheduleDatabase(app: Application): ScheduleDatabase {
        return Room.databaseBuilder(
            app,
            ScheduleDatabase::class.java,
            SCHEDULE_COLLECTION
        ).build()
    }

    @Singleton
    @Provides
    fun provideScheduleRepository(db: ScheduleDatabase): ScheduleRepository {
        return ScheduleRepositoryImpl(db.dao)
    }
}