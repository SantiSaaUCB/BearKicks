package com.bearkicks.app.features.auth.data.repository

import com.bearkicks.app.features.auth.data.datastore.AuthDataStore
import com.bearkicks.app.features.auth.data.datasource.AuthFirebaseDataSource
import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AuthRepository(
    private val ds: AuthFirebaseDataSource,
    private val store: AuthDataStore
) : IAuthRepository {
    override suspend fun login(email: String, password: String): Result<UserModel> = runCatching {
        val user = ds.login(email, password)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoUrl)
        user
    }

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        password: String,
        photoUrl: String?
    ): Result<UserModel> = runCatching {
        val user = ds.register(firstName, lastName, email, phone, address, password, photoUrl)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoUrl)
        user
    }

    override fun observeUser(): Flow<UserModel?> = ds.observeUser().onEach { user ->
        if (user == null) store.clear() else store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoUrl)
    }

    override suspend fun getCurrentUser(): UserModel? = ds.getCurrentUser()
    override suspend fun logout(): Result<Unit> = runCatching { ds.logout(); store.clear() }

    override suspend fun updateProfile(
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?,
        photoUrl: String?
    ): Result<UserModel> = runCatching {
        val user = ds.updateProfile(firstName, lastName, phone, address, photoUrl)
        store.saveSession(user.id, user.email, "${user.firstName} ${user.lastName}".trim(), user.photoUrl)
        user
    }
}
