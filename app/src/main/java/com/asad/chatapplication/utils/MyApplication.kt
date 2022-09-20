package com.asad.chatapplication.utils

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.asad.chatapplication.utils.StaticFunctions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onAppBackgrounded() {
        Log.d("MyApp", "App in background")
        setStatus("offline")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onAppForegrounded() {
        Log.d("MyApp", "App in foreground")
        setStatus("online")
    }

    private fun setStatus(userStatus: String) {
        Handler(Looper.getMainLooper()).post(Runnable {
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("Statuses")
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["status"] = userStatus
                hashMap["lastSeen"] = StaticFunctions.GetCurrentDateAndTime()
                userRef.child(firebaseUser.uid).setValue(hashMap)
            }
        })
    }
}