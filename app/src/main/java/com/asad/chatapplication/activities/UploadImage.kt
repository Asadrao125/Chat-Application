package com.asad.chatapplication.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.adapters.ImageAdapter

class UploadImage : AppCompatActivity() {
    var selectedFilesList: ArrayList<Uri> = ArrayList()
    var recyclerView: RecyclerView? = null

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        val btn: Button = findViewById(R.id.btn)
        recyclerView = findViewById(R.id.recyclerView)

        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)

        btn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 11)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 11
            && resultCode == RESULT_OK
            && data != null
        ) {
            selectedFilesList.clear()
            if (data.data != null) {
                selectedFilesList.add(data.data!!)
            } else {
                val count: Int = data.getClipData()!!.getItemCount()
                if (count > 1) {
                    for (i in 0 until count) {
                        val imageUri: Uri = data.getClipData()!!.getItemAt(i).getUri()
                        selectedFilesList.add(imageUri)
                    }
                } else {
                    selectedFilesList.add(data.data!!)
                }
            }

            recyclerView?.adapter = ImageAdapter(applicationContext, selectedFilesList)
        }
    }
}