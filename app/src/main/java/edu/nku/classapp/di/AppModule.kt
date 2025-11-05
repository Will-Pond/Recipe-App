package edu.nku.classapp.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import edu.nku.classapp.data.FoodApi
import edu.nku.classapp.data.api.ImgurApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFoodApi(): FoodApi =
        Retrofit.Builder().baseUrl("https://world.openfoodfacts.org/api/v2/").addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        ).build().create()

    @Provides
    @Singleton
    fun provideImgurApi(): ImgurApi =
        Retrofit.Builder()
            .baseUrl("https://api.imgur.com/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(ImgurApi::class.java)
}