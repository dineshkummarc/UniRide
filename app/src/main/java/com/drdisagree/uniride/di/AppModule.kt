package com.drdisagree.uniride.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.drdisagree.uniride.data.database.ScheduleDatabase
import com.drdisagree.uniride.data.utils.Constant.SCHEDULE_COLLECTION
import com.drdisagree.uniride.domain.repository.ScheduleRepository
import com.drdisagree.uniride.domain.repository.ScheduleRepositoryImpl
import com.drdisagree.uniride.services.GeocodingService
import com.drdisagree.uniride.utils.repositories.GeocodingRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun provideSignInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }

    @Provides
    fun provideViewModelFactory(
        @ApplicationContext context: Context
    ): ViewModelProvider.Factory {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application)
    }

    @Provides
    fun provideGeocodingService(): GeocodingService {
        return Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingService::class.java)
    }

    @Provides
    @Singleton
    fun provideGeocodingRepository(geocodingService: GeocodingService): GeocodingRepository {
        return GeocodingRepository(geocodingService)
    }
}