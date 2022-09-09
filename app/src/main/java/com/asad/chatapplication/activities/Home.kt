package com.asad.chatapplication.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.UserAdapter
import com.asad.chatapplication.models.UserModel
import com.asad.chatapplication.utils.Dialog_CustomProgress
import com.asad.chatapplication.utils.StaticFunctions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso

class Home : AppCompatActivity() {
    var recyclerViewUsers: RecyclerView? = null
    var adapter: UserAdapter? = null
    var profilePic: ImageView? = null
    var list: ArrayList<UserModel>? = null
    var mAuth: FirebaseAuth? = null
    var tvName: TextView? = null
    var profileUrl: String = ""
    var senderId: String = ""
    var recieverId: String = ""
    var userName: String = ""
    var customProgressDialog: Dialog_CustomProgress? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)
        setTitle("Home")

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers)
        recyclerViewUsers?.layoutManager = LinearLayoutManager(this)
        recyclerViewUsers?.setHasFixedSize(true)
        list = ArrayList()
        tvName = findViewById(R.id.tvName)
        profilePic = findViewById(R.id.profilePic)
        mAuth = FirebaseAuth.getInstance()
        customProgressDialog = Dialog_CustomProgress(this)

        getAllUsers()
        getFirebaseToken()

        profilePic?.setOnClickListener {
            if (!profileUrl.isEmpty()) {
                val intent = Intent(applicationContext, ViewImage::class.java)
                intent.putExtra("imageUrl", profileUrl)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val transition =
                    Pair.create<View?, String?>(profilePic, "transition")
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@Home, transition
                    )
                startActivity(intent, options.toBundle())
            }
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                val reference =
                    FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
                reference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                        val user: UserModel = dataSnapshot.getValue(UserModel::class.java)!!
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["id"] = user.id
                        hashMap["name"] = user.name
                        hashMap["email"] = user.email
                        hashMap["password"] = user.password
                        hashMap["token"] = token
                        hashMap["profilePic"] = user.profilePic
                        reference.setValue(hashMap)
                    }

                    override fun onCancelled(@NonNull databaseError: DatabaseError) {}
                })
            } else {
                StaticFunctions.ShowToast(applicationContext, task.exception?.localizedMessage!!)
            }
        })
    }

    private fun getAllUsers() {
        list!!.clear()
        customProgressDialog?.show()
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                list!!.clear()
                customProgressDialog?.dismiss()
                for (snapshot in dataSnapshot.children) {
                    val user: UserModel = snapshot.getValue(UserModel::class.java)!!
                    list!!.add(user)
                    if (user.id.equals(firebaseUser!!.uid)) {
                        profileUrl = user.profilePic
                        senderId = firebaseUser.uid
                        recieverId = user.id
                        userName = user.name
                        tvName?.text = user.name
                        Picasso.get().load(profileUrl).placeholder(R.drawable.ic_user)
                            .into(profilePic)
                    }
                }
                adapter = UserAdapter(this@Home, list!!, userName)
                recyclerViewUsers?.setAdapter(adapter)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun onStart() {
        super.onStart()
        setStatus("online", 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if (id == R.id.logoutMenu) {
            setStatus("offline", 1)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setStatus(userStatus: String, logoutOrSetStatus: Int) {
        Handler(Looper.getMainLooper()).post(Runnable {
            val userRef: DatabaseReference =
                FirebaseDatabase.getInstance().getReference().child("Statuses")
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = auth.currentUser

            if (logoutOrSetStatus == 1) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["status"] = userStatus
                hashMap["lastSeen"] = StaticFunctions.GetCurrentDateAndTime()
                userRef.child(firebaseUser?.uid!!).setValue(hashMap)
                mAuth?.signOut()
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            } else {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap["status"] = userStatus
                hashMap["lastSeen"] = StaticFunctions.GetCurrentDateAndTime()
                userRef.child(firebaseUser?.uid!!).setValue(hashMap)
            }
        })
    }
}