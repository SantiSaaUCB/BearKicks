package com.bearkicks.app.features.auth.domain.model.vo

@JvmInline
value class Name private constructor(val value: String) {
    companion object {
        private val NAME_REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' -]{2,40}$")
        fun create(input: String): Result<Name> {
            val v = input.trim()
            return if (NAME_REGEX.matches(v)) Result.success(Name(v))
            else Result.failure(IllegalArgumentException("Nombre inválido"))
        }
    }
}

@JvmInline
value class LastName private constructor(val value: String) {
    companion object {
        private val NAME_REGEX = Regex("^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ' -]{2,40}$")
        fun create(input: String): Result<LastName> {
            val v = input.trim()
            return if (NAME_REGEX.matches(v)) Result.success(LastName(v))
            else Result.failure(IllegalArgumentException("Apellido inválido"))
        }
    }
}
