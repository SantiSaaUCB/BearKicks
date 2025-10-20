package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Phone private constructor(val value: String) {
    companion object {
        private val PHONE_REGEX = Regex("^[0-9]{8}$") // Cochabamba: 8 dígitos
        fun create(input: String): Result<Phone> =
            if (PHONE_REGEX.matches(input.trim())) Result.success(Phone(input.trim()))
            else Result.failure(IllegalArgumentException("Teléfono inválido (8 dígitos)"))
    }
}

@JvmInline
value class Address private constructor(val value: String) {
    companion object {
        // Letras, números, espacios y separadores comunes; 5-80 caracteres
        private val ADDRESS_REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9#.,'°-][A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 #.,'°-]{4,79}$")
        fun create(input: String): Result<Address> {
            val v = input.trim()
            return if (ADDRESS_REGEX.matches(v)) Result.success(Address(v))
            else Result.failure(IllegalArgumentException("Dirección inválida"))
        }
    }
}
