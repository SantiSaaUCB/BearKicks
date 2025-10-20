package com.bearkicks.app.features.wishlist.data.datasource

import com.bearkicks.app.features.home.domain.model.ShoeModel
import com.google.firebase.database.DatabaseReference

// Actualiza like remoto en /shoes/{id}/isLiked y opcionalmente mantiene /favorites/{id}
class FavoritesRemoteDataSource(
    private val dbRoot: DatabaseReference,
    private val userIdProvider: () -> String
) {
    private fun favsRef(): DatabaseReference = dbRoot.child("favorites")
    private fun shoesRef(): DatabaseReference = dbRoot.child("shoes")
    private fun userFavsRef(uid: String): DatabaseReference = dbRoot.child("userFavorites").child(uid)

    suspend fun setFavorite(shoe: ShoeModel, favored: Boolean) {
        val uid = userIdProvider()
        // 1) Reflejar like en el nodo principal de zapatos
        val shoeNode = shoesRef().child(shoe.id)
        shoeNode.child("isLiked").setValue(favored)
        shoeNode.child("isliked").removeValue()

        // 2) Mantener una lista de favoritos simple (sin usuarios) para depurar
        val favNode = favsRef().child(shoe.id)
        if (favored) {
            val payload = mapOf(
                "id" to shoe.id,
                "name" to shoe.name,
                "brand" to shoe.brand,
                "price" to shoe.price,
                "discountPrice" to shoe.discountPrice,
                "imageUrl" to shoe.imageUrl,
                "isLiked" to true,
                "savedAt" to System.currentTimeMillis()
            )
            favNode.setValue(payload)
            // 3) Por usuario
            userFavsRef(uid).child(shoe.id).setValue(true)
        } else {
            favNode.removeValue()
            userFavsRef(uid).child(shoe.id).removeValue()
        }
    }
}
