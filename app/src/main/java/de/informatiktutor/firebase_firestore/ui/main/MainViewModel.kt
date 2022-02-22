package de.informatiktutor.firebase_firestore.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.informatiktutor.firebase_firestore.model.User

class MainViewModel : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.name
    }

    val usersLiveData = MutableLiveData<List<User>>()
    val userCreatedLiveData = MutableLiveData<Boolean>()

    private val db = Firebase.firestore

    fun loadUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val data = result.toObjects(User::class.java)
                usersLiveData.value = data.toList()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun createUser(user: User) {
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                userCreatedLiveData.value = true
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                userCreatedLiveData.value = false
            }
    }
}
