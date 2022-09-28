package com.asad.chatapplication.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.asad.chatapplication.R
import com.asad.chatapplication.models.UserModel
import com.asad.chatapplication.utils.StaticFunctions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.rengwuxian.materialedittext.MaterialEditText
import com.squareup.picasso.Picasso
import java.util.*

class EditProfile : AppCompatActivity() {
    var imgProfile: ImageView? = null
    var imgChangeImage: ImageView? = null
    var etName: MaterialEditText? = null
    var etEmail: MaterialEditText? = null
    var etPassword: MaterialEditText? = null
    var etAboutInfo: MaterialEditText? = null
    var btnUpdate: Button? = null
    var uid: String? = ""
    var imageUrl: String? = ""
    var Image_Request_Code = 7
    var userModel: UserModel? = null
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        setTitle("Edit Profile")
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setDisplayShowHomeEnabled(true)

        imgProfile = findViewById(R.id.imgProfile)
        imgChangeImage = findViewById(R.id.imgChangeImage)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etAboutInfo = findViewById(R.id.etAboutInfo)
        btnUpdate = findViewById(R.id.btnUpdate)

        setFirebaseData()

        btnUpdate?.setOnClickListener(View.OnClickListener {
            val name = etName?.text.toString().trim()
            val aboutInfo = etAboutInfo?.text.toString().trim()
            if (name.isEmpty()) {
                StaticFunctions.ShowToast(applicationContext, "Please Enter Name")
            } else if (aboutInfo.isEmpty()) {
                StaticFunctions.ShowToast(applicationContext, "Please Enter About Info")
            } else {
                updateFirebaseData(name, aboutInfo)
            }
        })

        imgChangeImage?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, Image_Request_Code)
        }
    }

    fun updateFirebaseData(name: String, aboutInfo: String) {
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user: UserModel = dataSnapshot.getValue(UserModel::class.java)!!
                reference!!.child("name").setValue(name)
                reference!!.child("aboutInfo").setValue(aboutInfo)
                etPassword?.setText(user.password)
                etEmail?.setText(user.email)
                etName?.setText(user.name)
                Picasso.get().load(user.profilePic).placeholder(R.drawable.ic_user).into(imgProfile)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Image_Request_Code
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            val file_uri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, file_uri)
            imgProfile?.setImageBitmap(bitmap)
            uploadImageToFirebase(file_uri!!)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("profilePictures/$fileName")
        refStorage.putFile(fileUri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        imageUrl = it.toString()
                        reference?.child("profilePic")?.setValue(imageUrl)
                    }
                }).addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }

    fun setFirebaseData() {
        reference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                val user: UserModel = dataSnapshot.getValue(UserModel::class.java)!!
                userModel = user
                etPassword?.setText(user.password)
                etEmail?.setText(user.email)
                etName?.setText(user.name)
                etAboutInfo?.setText(user.aboutInfo)
                Picasso.get().load(user.profilePic).placeholder(R.drawable.ic_user).into(imgProfile)

                imgProfile?.setOnClickListener {
                    val intent = Intent(applicationContext, ViewImage::class.java)
                    intent.putExtra("imageUrl", user.profilePic)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val transition =
                        Pair.create<View?, String?>(imgProfile, "transition")
                    val options =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this@EditProfile, transition
                        )
                    startActivity(intent, options.toBundle())
                }
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