package com.aki.flashnews.di

import com.aki.flashnews.data.repository.NewsRepositoryImpl
import com.aki.flashnews.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton// RepositoryImpl is already Singleton, this ensures consistency
    abstract fun bindNewsRepository(
        newsRepositoryImpl: NewsRepositoryImpl
    ): NewsRepository
}