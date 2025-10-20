package com.bearkicks.app.features.auth.data.datasource

import com.bearkicks.app.features.auth.domain.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class ProfileDto(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val photoUrl: String? = null
)

class AuthFirebaseDataSource(
    private val auth: FirebaseAuth,
    private val usersRef: DatabaseReference
) {
    private fun userNode(uid: String) = usersRef.child(uid)

    fun observeUser(): Flow<UserModel?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { fba ->
            val user = fba.currentUser
            if (user == null) {
                trySend(null)
            } else {
                userNode(user.uid).get().addOnSuccessListener { snap ->
                    val dto = snap.getValue(ProfileDto::class.java)
                    trySend(
                        UserModel(
                            id = user.uid,
                            firstName = dto?.firstName ?: "",
                            lastName = dto?.lastName ?: "",
                            email = dto?.email ?: user.email.orEmpty(),
                            phone = dto?.phone,
                            address = dto?.address,
                            photoUrl = dto?.photoUrl
                        )
                    )
                }.addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(authListener)
        awaitClose { auth.removeAuthStateListener(authListener) }
    }

    suspend fun getCurrentUser(): UserModel? {
        val user = auth.currentUser ?: return null
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            photoUrl = dto?.photoUrl
        )
    }

    suspend fun login(email: String, password: String): UserModel {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No se pudo iniciar sesión")
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            photoUrl = dto?.photoUrl
        )
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        phone: String?,
        address: String?,
        password: String,
        photoUrl: String?
    ): UserModel {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw IllegalStateException("No se pudo crear el usuario")

        val payload = ProfileDto(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            address = address,
            photoUrl = photoUrl
        )
        userNode(user.uid).setValue(payload).await()
        return UserModel(
            id = user.uid,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            address = address,
            photoUrl = photoUrl
        )
    }

    suspend fun logout() { auth.signOut() }

    suspend fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phone: String? = null,
        address: String? = null,
        photoUrl: String? = null
    ): UserModel {
        val user = auth.currentUser ?: throw IllegalStateException("No autenticado")
        val updates = mutableMapOf<String, Any?>()
        if (firstName != null) updates["firstName"] = firstName
        if (lastName != null) updates["lastName"] = lastName
        if (phone != null) updates["phone"] = phone
        if (address != null) updates["address"] = address
        if (photoUrl != null) updates["photoUrl"] = photoUrl
        if (updates.isNotEmpty()) userNode(user.uid).updateChildren(updates).await()
        val dto = userNode(user.uid).get().await().getValue(ProfileDto::class.java)
        return UserModel(
            id = user.uid,
            firstName = dto?.firstName ?: "",
            lastName = dto?.lastName ?: "",
            email = dto?.email ?: user.email.orElse(),
            phone = dto?.phone,
            address = dto?.address,
            photoUrl = dto?.photoUrl
        )
    }
}

private fun String?.orElse(default: String = ""): String = this ?: default
