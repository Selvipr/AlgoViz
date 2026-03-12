package com.example.algoviz.di

import com.example.algoviz.data.repository.AuthRepositoryImpl
import com.example.algoviz.data.repository.SettingsRepositoryImpl
import com.example.algoviz.data.repository.TopicRepositoryImpl
import com.example.algoviz.data.repository.UserRepositoryImpl
import com.example.algoviz.domain.repository.AuthRepository
import com.example.algoviz.domain.repository.SettingsRepository
import com.example.algoviz.domain.repository.TopicRepository
import com.example.algoviz.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindTopicRepository(impl: TopicRepositoryImpl): TopicRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
