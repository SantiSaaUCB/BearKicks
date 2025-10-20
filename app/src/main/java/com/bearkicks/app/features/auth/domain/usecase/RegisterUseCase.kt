package com.bearkicks.app.features.auth.domain.usecase

import com.bearkicks.app.features.auth.domain.model.vo.Address
import com.bearkicks.app.features.auth.domain.model.vo.Email
import com.bearkicks.app.features.auth.domain.model.vo.LastName
import com.bearkicks.app.features.auth.domain.model.vo.Name
import com.bearkicks.app.features.auth.domain.model.vo.Password
import com.bearkicks.app.features.auth.domain.model.vo.Phone
import com.bearkicks.app.features.auth.domain.repository.IAuthRepository

class RegisterUseCase(private val repo: IAuthRepository) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        password: String,
        photoUrl: String? = null
    ) =
        Name.create(firstName)
            .flatMap { LastName.create(lastName) }
            .flatMap { Email.create(email) }
            .flatMap { if (phone.isNullOrBlank()) Result.success(Unit) else Phone.create(phone).map {} }
            .flatMap { if (address.isNullOrBlank()) Result.success(Unit) else Address.create(address).map {} }
            .flatMap { Password.create(password).map {} }
            .fold(
                onSuccess = {
                    repo.register(
                        firstName.trim(), lastName.trim(), email.trim(),
                        phone?.trim(), address?.trim(), password.trim(), photoUrl
                    )
                },
                onFailure = { Result.failure(it) }
            )
}

private inline fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> =
    fold(onSuccess = transform, onFailure = { Result.failure(it) })
