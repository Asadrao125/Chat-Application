package com.asad.chatapplication.testActivities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.StoryAdapter
import com.asad.chatapplication.models.StoryModal
import com.asad.chatapplication.utils.StaticFunctions.Companion.GetCurrentDate
import com.asad.chatapplication.utils.StaticFunctions.Companion.GetCurrentTime
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.util.*

class StoryTestActivity : AppCompatActivity() {
    var Image_Request_Code = 7
    var btnSelectImage: Button? = null
    var storyList: ArrayList<StoryModal>? = ArrayList()
    var storyRv: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_test)

        storyRv = findViewById(R.id.storyRv)
        val linearLayoutManager = LinearLayoutManager(this)
        storyRv?.layoutManager = linearLayoutManager
        storyRv?.setHasFixedSize(true)

        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnSelectImage?.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, Image_Request_Code)
        }

        getStories()
    }

    private fun getStories() {
        val storyRefrence: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Stories")
        storyRefrence.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                storyList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val storyModal: StoryModal = snapshot.getValue(StoryModal::class.java)!!
                    storyList!!.add(storyModal)
                }
                storyRv!!.adapter = StoryAdapter(this@StoryTestActivity, storyList!!)
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {

            }
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
            uploadImageToFirebase(file_uri!!)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val storyRefrence: DatabaseReference =
            FirebaseDatabase.getInstance().getReference().child("Stories")
        val storyId = storyRefrence.push().key

        val fileName = UUID.randomUUID().toString() + ".jpg"
        val refStorage = FirebaseStorage.getInstance().reference.child("userStatuses/$fileName")
        refStorage.putFile(fileUri)
            .addOnSuccessListener(
                OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val storyImageUrl: String = it.toString()
                        val storyUploaderName = "Asad Rao"
                        val storyModal =
                            StoryModal(
                                storyUploaderName,
                                storyId!!,
                                storyImageUrl,
                                GetCurrentDate(),
                                GetCurrentTime()
                            )
                        storyRefrence.child(storyId).setValue(storyModal)
                    }
                }).addOnFailureListener(OnFailureListener { e ->
                print(e.message)
            })
    }
}