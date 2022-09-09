package com.asad.chatapplication.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.activities.Chat
import com.asad.chatapplication.activities.Home
import com.asad.chatapplication.activities.ViewImage
import com.asad.chatapplication.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class UserAdapter(var context: Home, var list: ArrayList<UserModel>, var username: String) :
    RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val userModel: UserModel = list.get(position)
        Picasso.get().load(userModel.profilePic).placeholder(R.drawable.ic_user)
            .into(holder.profilePic)
        holder.tvUserName.text = userModel.name

        holder.profilePic.setOnClickListener {

            val intent = Intent(context, ViewImage::class.java)
            intent.putExtra("imageUrl", userModel.profilePic)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val transition =
                Pair.create<View?, String?>(holder.profilePic, "transition")
            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context, transition
                )
            context.startActivity(intent, options.toBundle())
        }

        if (userModel.id.equals(firebaseUser!!.uid)) {
            holder.tvUserName.text = "You"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("senderId", firebaseUser.uid)
            intent.putExtra("recieverId", userModel.id)
            intent.putExtra("username", userModel.name)
            intent.putExtra("fcmToken", userModel.token)
            intent.putExtra("senderName", username)
            intent.putExtra("profilePic", list.get(position).profilePic)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        checkUserStatus(userModel.id, holder)
    }

    private fun checkUserStatus(id: String, holder: MyViewHolder) {
        val userRef: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Statuses")
        userRef.child(id).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val status: String = dataSnapshot.child("status").value.toString()
                if (!status.isEmpty()) {
                    holder.imgOnline.visibility = View.VISIBLE
                    if (status.equals("online")) {
                        holder.imgOnline.setImageResource(R.drawable.online_circle)
                    } else {
                        holder.imgOnline.setImageResource(R.drawable.offline_circle)
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUserName: TextView
        var profilePic: ImageView
        var imgOnline: ImageView

        init {
            tvUserName = itemView.findViewById(R.id.tvUserName)
            profilePic = itemView.findViewById(R.id.profilePic)
            imgOnline = itemView.findViewById(R.id.imgOnline)
        }
    }
}