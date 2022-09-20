package com.asad.chatapplication.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.UserAdapter
import com.asad.chatapplication.models.UserModel
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ViewProfile : AppCompatActivity() {
    var tvName: TextView? = null
    var tvEmail: TextView? = null
    var tvAboutInfo: TextView? = null
    var profilePic: CircleImageView? = null
    var id: String? = ""
    var profilePicUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        setTitle("View Profile")

        tvName = findViewById(R.id.tvName)
        tvEmail = findViewById(R.id.tvEmail)
        tvAboutInfo = findViewById(R.id.tvAboutInfo)
        profilePic = findViewById(R.id.profilePic)
        id = intent.getStringExtra("id")

        setProfile(id!!)

        profilePic?.setOnClickListener {
            if (!profilePicUrl!!.isEmpty()) {
                val intent = Intent(applicationContext, ViewImage::class.java)
                intent.putExtra("imageUrl", profilePicUrl)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val transition =
                    Pair.create<View?, String?>(profilePic, "transition")
                val options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        this@ViewProfile, transition
                    )
                startActivity(intent, options.toBundle())
            }
        }

    }

    private fun setProfile(id: String) {
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user: UserModel = dataSnapshot.getValue(UserModel::class.java)!!
                tvName?.setText(user.name)
                tvEmail?.setText(user.email)
                tvAboutInfo?.setText(user.aboutInfo)
                profilePicUrl = user.profilePic
                Picasso.get().load(user.profilePic).placeholder(R.drawable.ic_user).into(profilePic)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}