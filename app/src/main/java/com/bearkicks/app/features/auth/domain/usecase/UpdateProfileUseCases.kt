package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository

class UpdateProfileUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        address: String? = null
    ): Result<UserModel> = repo.updateProfile(firstName, lastName, phone, address, null)
}

class UpdateProfilePhotoUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(photoUrl: String): Result<UserModel> = repo.updateProfile(photoUrl = photoUrl)
}
