package de.informatiktutor.firebase_firestore.model

data class User(
    val firstName: String,
    val lastName: String
) {
    constructor() : this("", "") {}
}
