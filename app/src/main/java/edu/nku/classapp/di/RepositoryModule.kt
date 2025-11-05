package edu.nku.classapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.nku.classapp.data.repository.FoodRepository
import edu.nku.classapp.data.repository.FoodRepositoryReal
import edu.nku.classapp.data.repository.ImgurPostRepository
import edu.nku.classapp.data.repository.ImgurPostRepositoryReal
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        foodRepositoryReal: FoodRepositoryReal
    ): FoodRepository

    @Binds
    @Singleton
    abstract fun bindImgurPostRepository(
        imgurPostRepositoryReal: ImgurPostRepositoryReal
    ): ImgurPostRepository
}