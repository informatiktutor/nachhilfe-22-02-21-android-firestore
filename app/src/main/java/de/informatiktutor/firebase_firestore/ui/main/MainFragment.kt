package de.informatiktutor.firebase_firestore.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import de.informatiktutor.firebase_firestore.R
import de.informatiktutor.firebase_firestore.model.PickerDate
import de.informatiktutor.firebase_firestore.model.User

class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"

        fun newInstance() = MainFragment()
    }

    val db = Firebase.firestore

    private lateinit var viewModel: MainViewModel

    // TODO: Databinding verwenden
    private lateinit var vornameEditText: EditText
    private lateinit var nachnameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var allUsersList: ListView

    private val usersList = ArrayList<String>()
    private lateinit var userListAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    private lateinit var datePickerButton: Button
    private var pickedDate: PickerDate? = null

    private fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment { date ->
            pickedDate = date
            datePickerButton.text = "${date.day}.${date.month + 1}.${date.year}"
            Log.w(TAG, "User picked a date: $pickedDate")
        }
        newFragment.show(childFragmentManager, "datePicker")
    }

    private fun onSaveClicked(view: View) {
        val firstName = vornameEditText.text.toString()
        val lastName = nachnameEditText.text.toString()
        viewModel.createUser(User(firstName, lastName))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vornameEditText = view.findViewById(R.id.editTextVorname)
        nachnameEditText = view.findViewById(R.id.editTextNachname)
        saveButton = view.findViewById(R.id.saveButton)
        allUsersList = view.findViewById(R.id.allUsersList)

        datePickerButton = view.findViewById(R.id.datePickerButton)
        datePickerButton.setOnClickListener(this::showDatePickerDialog)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // 1. Get user input on button click
        saveButton.setOnClickListener(this::onSaveClicked)

        userListAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, usersList)
        allUsersList.adapter = userListAdapter

        // 2. Show database data in a list
        viewModel.usersLiveData.observe(viewLifecycleOwner) { users ->
            usersList.clear()
            usersList.addAll(users.map { user ->
                "${user.firstName} ${user.lastName}"
            })
            userListAdapter.notifyDataSetChanged()
        }

        viewModel.loadUsers()

        // Observer wird in onViewCreated erstellt, damit dieser nur einmal angelegt wird!
        viewModel.userCreatedLiveData.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                viewModel.loadUsers()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Konnte Nutzer nicht speichern!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815,
            "birth_place" to "Gummersbach"
        )

        val user = User("Frank", "Maurer")

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        db.collection("users")
            .document("SyY3xA3V5xZ2ng8Uct9i")
            .update("born", 1715)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

        db.collection("users")
            .document("x3XSy4n2yeCMCQ0FHBT6")
            .set(User("Stella", "Fante"))

        db.collection("users")
            .document("x3XSy4n2yeCMCQ0FHBT6")
            .get()
            .addOnSuccessListener { result ->
                val user = result.toObject(User::class.java)
                Log.w(TAG, "User: " + user.toString())
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
        */
    }
}
