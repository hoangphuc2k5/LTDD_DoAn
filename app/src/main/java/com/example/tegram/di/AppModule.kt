package com.example.tegram.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.tegram.R
import com.example.tegram.data.local.dao.UserDao
import com.example.tegram.data.local.database.TegramDatabase
import com.example.tegram.data.local.datastore.UserPreferencesDataStore
import com.example.tegram.data.remote.api.LearningApiService
import com.example.tegram.data.remote.api.UserApiService
import com.example.tegram.data.repository.LearningRepositoryImpl
import com.example.tegram.data.repository.UserRepositoryImpl
import com.example.tegram.domain.repository.LearningRepository
import com.example.tegram.domain.repository.UserRepository
import com.example.tegram.domain.usecase.learning.GetDailyPlanUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * App-level Hilt module — provides singleton-scoped dependencies
 * that live for the entire application lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth? = runCatching { FirebaseAuth.getInstance() }.getOrNull()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.userPreferencesDataStore

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TegramDatabase =
        Room.databaseBuilder(
            context,
            TegramDatabase::class.java,
            "tegram.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideUserDao(database: TegramDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideUserPreferencesDataStore(dataStore: DataStore<Preferences>): UserPreferencesDataStore =
        UserPreferencesDataStore(dataStore)

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideRetrofit(
        @ApplicationContext context: Context,
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(context.getString(R.string.api_base_url))
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideLearningApiService(retrofit: Retrofit): LearningApiService =
        retrofit.create(LearningApiService::class.java)

    @Provides
    @Singleton
    fun provideGetDailyPlanUseCase(): GetDailyPlanUseCase = GetDailyPlanUseCase()

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth?,
        userDao: UserDao,
        userPreferencesDataStore: UserPreferencesDataStore,
        userApiService: UserApiService
    ): UserRepository = UserRepositoryImpl(
        firebaseAuth = firebaseAuth,
        userDao = userDao,
        userPreferencesDataStore = userPreferencesDataStore,
        userApiService = userApiService
    )

    @Provides
    @Singleton
    fun provideLearningRepository(
        learningApiService: LearningApiService,
        getDailyPlanUseCase: GetDailyPlanUseCase
    ): LearningRepository = LearningRepositoryImpl(
        learningApiService = learningApiService,
        dailyPlanUseCase = getDailyPlanUseCase
    )
}

private val Context.userPreferencesDataStore by preferencesDataStore(name = "tegram_user_preferences")
