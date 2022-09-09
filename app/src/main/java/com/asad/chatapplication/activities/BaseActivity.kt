package com.asad.chatapplication.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun setStatus(userStatus: String) {
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Statuses")
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["status"] = userStatus
        userRef.child(firebaseUser!!.uid).setValue(hashMap)
    }

    override fun onResume() {
        super.onResume()
        setStatus("online")
    }

    override fun onStop() {
        super.onStop()
        setStatus("offline")
    }
}