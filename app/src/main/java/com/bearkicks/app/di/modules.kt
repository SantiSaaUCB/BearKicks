package com.bearkicks.app.di

import androidx.room.Room
import com.bearkicks.app.features.home.data.datasource.HomeRemoteDataSource
import com.bearkicks.app.features.home.data.repository.HomeRepository
import com.bearkicks.app.features.home.domain.repository.IHomeRepository
import com.bearkicks.app.features.home.domain.usecase.GetNewShoesUseCase
import com.bearkicks.app.features.home.domain.usecase.GetOfferShoesUseCase
import com.bearkicks.app.features.home.presentation.HomeViewModel
import com.bearkicks.app.features.shop.data.datasource.ShopRemoteDataSource
import com.bearkicks.app.features.shop.data.repository.ShopRepository
import com.bearkicks.app.features.shop.domain.repository.IShopRepository
import com.bearkicks.app.features.shop.domain.usecase.GetAllShoesUseCase
import com.bearkicks.app.features.shop.domain.usecase.GetShoeDetailUseCase
import com.bearkicks.app.features.shop.presentation.ShoeDetailViewModel
import com.bearkicks.app.features.shop.presentation.ShopViewModel
import com.bearkicks.app.features.wishlist.data.database.WishListRoomDataBase
import com.bearkicks.app.features.wishlist.data.datasource.FavoritesRemoteDataSource
import com.bearkicks.app.features.wishlist.data.datasource.WishListLocalDataSource
import com.bearkicks.app.features.wishlist.data.repository.WishListRepository
import com.bearkicks.app.features.wishlist.domain.repository.IWishListRepository
import com.bearkicks.app.features.wishlist.domain.usecase.ObserveWishListUseCase
import com.bearkicks.app.features.wishlist.domain.usecase.ToggleWishListUseCase
import com.bearkicks.app.features.wishlist.presentation.WishListViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    // Firebase references
    single<DatabaseReference>(named("dbRoot")) { FirebaseDatabase.getInstance().reference }
    single<DatabaseReference>(named("shoesRef")) { get<DatabaseReference>(named("dbRoot")).child("shoes") }

    single { HomeRemoteDataSource(get(named("shoesRef"))) }
    single<IHomeRepository> { HomeRepository(get()) }
    factory { GetOfferShoesUseCase(get()) }
    factory { GetNewShoesUseCase(get()) }

    single { ShopRemoteDataSource(get(named("shoesRef"))) }
    single<IShopRepository> { ShopRepository(get()) }
    factory { GetAllShoesUseCase(get()) }
    factory { GetShoeDetailUseCase(get()) }

    single {
        Room.databaseBuilder(get(), WishListRoomDataBase::class.java, "wishlist.db")
            .fallbackToDestructiveMigration()
            .build()
            .favoriteDao()
    }
    single { WishListLocalDataSource(get()) }
    // FavoritesRemoteDataSource needs root to write to /shoes and /favorites
    single { FavoritesRemoteDataSource(get(named("dbRoot"))) }
    single<IWishListRepository> { WishListRepository(get(), get()) }
    factory { ObserveWishListUseCase(get()) }
    factory { ToggleWishListUseCase(get()) }

    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { ShopViewModel(get(), get(), get()) }
    viewModel { WishListViewModel(get(), get()) }
    viewModel { (id: String) -> ShoeDetailViewModel(id, get(), get(), get()) }
}
